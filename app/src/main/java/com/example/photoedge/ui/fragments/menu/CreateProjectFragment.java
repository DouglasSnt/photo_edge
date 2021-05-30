package com.example.photoedge.ui.fragments.menu;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.photoedge.R;
import com.example.photoedge.helpers.ProjectManager;
import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.activity.MainActivity;
import com.example.photoedge.ui.customview.RatioValues;
import com.example.photoedge.ui.customview.TemplateRatioView;
import com.example.photoedge.ui.list.RatioValuesAdapter;
import com.example.photoedge.ui.list.RatioValuesAdapter.RatioChangeListener;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateProjectFragment extends Fragment {

    private int lastWidth = 1280;
    private int lastHeight = 720;
    private final int minSize = 16;
    private final int maxSize = 4000;
    private int ratioWidth = 16;
    private int ratioHeight = 9;
    private Bitmap importedBitmap;
    private Spinner rSpinner;
    private TemplateRatioView rTemplate;
    private TextInputEditText inputHeight;
    private TextInputEditText inputWidth;
    private AppCompatImageButton cancelImageBtn;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                try {
                    RatioValuesAdapter adapter = (RatioValuesAdapter) rSpinner.getAdapter();
                    adapter.resetAdapter();

                    cancelImageBtn.setVisibility(View.VISIBLE);
                    Bitmap immutable = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    Bitmap workingBitmap = Bitmap.createBitmap(immutable);
                    importedBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

                    rTemplate.setImageBitmap(importedBitmap);

                    lastWidth = importedBitmap.getWidth();
                    lastHeight = importedBitmap.getHeight();

                    inputWidth.setText(String.valueOf(lastWidth));
                    inputHeight.setText(String.valueOf(lastHeight));
                    rTemplate.setPreviewSize(lastWidth, lastHeight);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    );

    private final RatioChangeListener ratioListener = (String ratio) -> {
        String[] r = ratio.split(":");
        if (r.length == 2) {
            ratioWidth = Integer.parseInt(r[0].trim());
            ratioHeight = Integer.parseInt(r[1].trim());
            lastHeight = (lastWidth / ratioWidth) * ratioHeight;
        }
        inputHeight.setText(String.valueOf(lastHeight));
        rTemplate.setPreviewSize(lastWidth, lastHeight);
    };

    public CreateProjectFragment() {
        super(R.layout.creating_project_fragment);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rTemplate = view.findViewById(R.id.ratio_template);
        inputWidth = view.findViewById(R.id.input_project_width);
        inputHeight = view.findViewById(R.id.input_project_height);
        cancelImageBtn = view.findViewById(R.id.cancel_create_from_image_btn);
        rSpinner = view.findViewById(R.id.ratio_spinner);
        TextInputEditText projName = view.findViewById(R.id.input_project_name);
        View createProjectBtn = view.findViewById(R.id.create_project_btn);
        View createFromImgBtn = view.findViewById(R.id.create_from_image_btn);

        projName.setSingleLine(true);

        FragmentActivity activity = requireActivity();

        Handler mHandler = new Handler(activity.getMainLooper());
        List<RatioValues> rList = new ArrayList<>(Arrays.asList(
                new RatioValues(getString(R.string.free), getString(R.string.free)),
                new RatioValues("16 : 9", "9 : 16"),
                new RatioValues("21 : 9", "9 : 21"),
                new RatioValues("5 : 4", "4 : 5"),
                new RatioValues("4 : 3", "3 : 4"),
                new RatioValues("3 : 2", "2 : 3"),
                new RatioValues("2 : 1", "1 : 2"),
                new RatioValues("1 : 1")
        ));

        RatioValuesAdapter adapter = new RatioValuesAdapter(activity, R.layout.ratio_table_item, rList);

        createFromImgBtn.setOnClickListener(v -> mGetContent.launch("image/*"));

        cancelImageBtn.setOnClickListener(v -> {
            cancelImageBtn.setVisibility(View.GONE);
            importedBitmap.recycle();
            importedBitmap = null;
            rTemplate.setImageResource(R.drawable.background_pattern);
        });

        createProjectBtn.setOnClickListener(v -> {
            String sw = inputWidth.getText().toString();
            String sh = inputHeight.getText().toString();
            boolean isValid = true;
            int w, h;
            if (!sh.isEmpty() && !sw.isEmpty()) {
                w = Integer.parseInt(sw);
                h = Integer.parseInt(sh);
            } else {
                w = h = 0;
            }

            if (w < minSize || w > maxSize) {
                isValid = false;
                inputWidth.setError(getString(R.string.create_project_invalid_width, minSize, maxSize));
            } else {
                inputWidth.setError(null);
            }

            if (h < minSize || h > maxSize) {
                isValid = false;
                inputHeight.setError(getString(R.string.create_project_invalid_height, minSize, maxSize));
            } else {
                inputHeight.setError(null);
            }

            if (isValid) {
                createProjectBtn.setClickable(false);
                MainActivity.SHARED_EXECUTOR.execute(() -> {

                    String projNameText = projName.getText().toString().trim();

                    String creationTimestamp = String.valueOf(System.currentTimeMillis());
                    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
                    String date = formatter.format(new Date(Long.parseLong(creationTimestamp)));

                    String projectName = (projNameText.isEmpty()) ? date : projNameText;

                    ProjectModel projectModel = new ProjectModel();
                    projectModel.setProjectName(projectName);
                    projectModel.setCreationDate(String.valueOf(System.currentTimeMillis()));
                    projectModel.setWidth(lastWidth);
                    projectModel.setHeight(lastHeight);
                    if (importedBitmap != null) projectModel.addLayer(importedBitmap, true);
                    else projectModel.addLayer();
                    projectModel.setCurrentZoom(1f);
                    projectModel.setPreTranslate(new PointF(0, 0));
                    projectModel.setCurrentLayer(0);

                    boolean created = ProjectManager.createProject(activity, projectModel);

                    if (created) {
                        activity.runOnUiThread(() -> {
                            ProjectViewModel model = new ViewModelProvider(activity).get(ProjectViewModel.class);
                            model.getProjectModel().setValue(projectModel);
                            Navigation.findNavController(activity, R.id.nav_host).navigate(R.id.action_createProjectFragment_to_canvasRootFragment);
                        });
                    }
                });
            }
        });

        inputWidth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty() || !inputWidth.hasFocus()) return;

                int newWidth = Integer.parseInt(s.toString());
                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(() -> {
                    lastWidth = newWidth;

                    String x = adapter.getSelectedRatio();
                    String[] r = x.split(":");
                    if (r.length == 2) {
                        ratioWidth = Integer.parseInt(r[0].trim());
                        ratioHeight = Integer.parseInt(r[1].trim());
                        lastHeight = (newWidth / ratioWidth) * ratioHeight;
                        inputHeight.setText(String.valueOf(lastHeight));
                    }
                    rTemplate.setPreviewSize(lastWidth, lastHeight);
                }, 1000);
            }
        });

        inputHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty() || !inputHeight.hasFocus()) return;

                int newHeight = Integer.parseInt(s.toString());

                mHandler.removeCallbacksAndMessages(null);
                mHandler.postDelayed(() -> {
                    lastHeight = newHeight;

                    String x = adapter.getSelectedRatio();
                    String[] r = x.split(":");
                    if (r.length == 2) {
                        ratioWidth = Integer.parseInt(r[0].trim());
                        ratioHeight = Integer.parseInt(r[1].trim());
                        lastWidth = (newHeight / ratioHeight) * ratioWidth;
                        inputWidth.setText(String.valueOf(lastWidth));
                    }

                    rTemplate.setPreviewSize(lastWidth, lastHeight);
                }, 500);
            }
        });

        rSpinner.setAdapter(adapter);
        adapter.setListener(ratioListener);
        rTemplate.post(() -> rTemplate.setPreviewSize(lastWidth, lastHeight));
    }

}
