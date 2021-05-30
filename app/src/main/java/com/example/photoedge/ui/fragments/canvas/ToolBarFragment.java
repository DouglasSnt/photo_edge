package com.example.photoedge.ui.fragments.canvas;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.enums.Enums;
import com.example.photoedge.helpers.ImageHelper;
import com.example.photoedge.helpers.ProjectManager;
import com.example.photoedge.ui.activity.MainActivity;
import com.example.photoedge.ui.customview.CropView;
import com.example.photoedge.ui.customview.TransformationView;
import com.example.photoedge.ui.tools.Tools;
import com.example.photoedge.ui.viewmodels.BlurToolViewModel;
import com.example.photoedge.ui.viewmodels.BrushToolViewModel;
import com.example.photoedge.ui.viewmodels.CropToolViewModel;
import com.example.photoedge.ui.viewmodels.EraserToolViewModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;
import com.example.photoedge.ui.viewmodels.ZoomToolAction;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ToolBarFragment extends Fragment {


    public ToolBarFragment() {
        super(R.layout.fragment_tool_bar);
    }

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                try {
                    ImageHelper importImageVm = new ImageHelper();
                    Bitmap importedBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                    ProjectViewModel projectViewModel = new ViewModelProvider(requireActivity()).get(ProjectViewModel.class);
                    importImageVm.importImageAsLayer(importedBitmap, projectViewModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    );

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity activity = requireActivity();

        ProjectViewModel projectViewModel = new ViewModelProvider(activity).get(ProjectViewModel.class);
        BrushToolViewModel brushToolViewModel = new ViewModelProvider(activity).get(BrushToolViewModel.class);
        EraserToolViewModel eraserToolViewModel = new ViewModelProvider(activity).get(EraserToolViewModel.class);
        BlurToolViewModel blurToolViewModel = new ViewModelProvider(activity).get(BlurToolViewModel.class);
        ZoomToolAction zoomToolViewModel = new ViewModelProvider(activity).get(ZoomToolAction.class);
        CropToolViewModel cropToolViewModel = new ViewModelProvider(activity).get(CropToolViewModel.class);

        activity.findViewById(R.id.brush_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setActiveTool(Tools.BRUSH_TOOl);
                    projectViewModel.setAction(brushToolViewModel.getToolAction(projectViewModel));
                }
        );

        activity.findViewById(R.id.eraser_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setActiveTool(Tools.ERASER_TOOL);
                    projectViewModel.setAction(eraserToolViewModel.getToolAction(projectViewModel));
                }
        );

        activity.findViewById(R.id.blur_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setActiveTool(Tools.BLUR_TOOL);
                    projectViewModel.setAction(blurToolViewModel.getToolAction(projectViewModel));
                }
        );

        activity.findViewById(R.id.zoom_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setActiveTool(Tools.ZOOM_TOOL);
                    projectViewModel.setAction(zoomToolViewModel.getToolAction(projectViewModel, activity));
                }
        );

        activity.findViewById(R.id.undo_tool_btn).setOnClickListener(v -> projectViewModel.undo());


        activity.findViewById(R.id.import_image_tool_btn).setOnClickListener(v ->
                mGetContent.launch("image/*")
        );

        activity.findViewById(R.id.export_image_btn).setOnClickListener(v -> MainActivity.SHARED_EXECUTOR.execute(() -> {
                    ProjectManager.exportImage(activity, projectViewModel.getProjectModel().getValue());
                    activity.runOnUiThread(() -> Toast.makeText(activity, getString(R.string.image_saved), Toast.LENGTH_SHORT).show());
                })
        );

        activity.findViewById(R.id.layer_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setActiveTool(Tools.LAYER_TOOL);
                    projectViewModel.setAction(null);
                }
        );

        activity.findViewById(R.id.transformation_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setAction(null);
                    projectViewModel.getProjectModel().getValue().setCurrentZoom(1f);
                    projectViewModel.getProjectModel().getValue().setPreTranslate(new PointF(0, 0));
                    projectViewModel.setActiveTool(Tools.TRANSFORMATION_TOOL);
                    TransformationView transformationArea = activity.findViewById(R.id.transformation_area_view);
                    transformationArea.setNewSelection(projectViewModel, this);
                    transformationArea.setVisibility(View.VISIBLE);
                    projectViewModel.setTransformationState(Enums.TransformationState.EDITING);
                }
        );

        activity.findViewById(R.id.crop_box_tool_btn).setOnClickListener(v -> {
                    projectViewModel.setAction(null);
                    projectViewModel.getProjectModel().getValue().setCurrentZoom(1f);
                    projectViewModel.getProjectModel().getValue().setPreTranslate(new PointF(0, 0));
                    projectViewModel.setActiveTool(Tools.CROP_TOOL);
                    CropView cropView = activity.findViewById(R.id.crop_view);
                    cropView.setNewSelection(projectViewModel, this);
                    cropView.setVisibility(View.VISIBLE);
                    cropView.setViewModel(cropToolViewModel, this);
                    projectViewModel.setTransformationState(Enums.TransformationState.EDITING);
                }
        );

        /*View brushToolBtn = activity.findViewById(R.id.brush_tool_btn);
        ScrollView toolBarScroll = (ScrollView) (activity.findViewById(R.id.tool_bar_scroll));
        activity.findViewById(R.id.tool_bar_scroll).post(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Integer tool = projectViewModel.getActiveTool().getValue();
            if (tool != null) return;
            brushToolBtn.callOnClick();
            toolBarScroll.fullScroll(View.FOCUS_DOWN);
        }, 500));*/

        activity.findViewById(R.id.save_btn).setOnClickListener(v ->
                MainActivity.SHARED_EXECUTOR.execute(() ->
                        ProjectManager.saveProject(requireActivity(), projectViewModel.getProjectModel().getValue())));

    }
}
