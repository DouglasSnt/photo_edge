package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.ui.customview.CropView;
import com.example.photoedge.ui.customview.TransformationView;
import com.example.photoedge.ui.tools.Tools;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import org.jetbrains.annotations.NotNull;

public class CanvasRootFragment extends Fragment {

    public CanvasRootFragment() {
        super(R.layout.fragment_root_canvas);
    }

    private final BrushToolFragment brushToolFragment = new BrushToolFragment();
    private final EraserToolFragment eraserToolFragment = new EraserToolFragment();
    private final BlurToolFragment blurToolFragment = new BlurToolFragment();
    private final EmptyToolFragment emptyToolFragment = new EmptyToolFragment();
    private final LayerToolFragment layerToolFragment = new LayerToolFragment();
    private final TransformToolFragment transformToolFragment = new TransformToolFragment();
    private final CropToolFragment cropToolFragment = new CropToolFragment();
    private boolean isShowingProp = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectViewModel model = new ViewModelProvider(requireActivity()).get(ProjectViewModel.class);
        model.getActiveTool().observe(this, this::setActiveFragmentTool);

        model.setActiveTool(Tools.EMPTY_TOOL);
        model.setAction(null);
        model.clearUndoList();

        final FragmentManager fm = getParentFragmentManager();
        fm.beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.root_canvas, CanvasFragment.class, null)
                .add(R.id.root_tool_bar, ToolBarFragment.class, null)
                .commit();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatImageButton showHidePropBtn = view.findViewById(R.id.show_hide_prop_btn);
        View propBar = view.findViewById(R.id.root_properties_bar);
        showHidePropBtn.setOnClickListener(v -> {
            if (isShowingProp) {
                propBar.setVisibility(View.GONE);
                showHidePropBtn.setImageResource(R.drawable.ic_arrow_left);
            } else {
                propBar.setVisibility(View.VISIBLE);
                showHidePropBtn.setImageResource(R.drawable.ic_arrow_right);
            }
            isShowingProp = !isShowingProp;
        });
    }

    private void setActiveFragmentTool(int tool) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);

        switch (tool) {
            case Tools.BRUSH_TOOl:
                transaction.replace(R.id.root_properties_bar, brushToolFragment, null);
                break;
            case Tools.ERASER_TOOL:
                transaction.replace(R.id.root_properties_bar, eraserToolFragment, null);
                break;
            case Tools.BLUR_TOOL:
                transaction.replace(R.id.root_properties_bar, blurToolFragment, null);
                break;
            case Tools.LAYER_TOOL:
                transaction.replace(R.id.root_properties_bar, layerToolFragment, null);
                break;
            case Tools.TRANSFORMATION_TOOL:
                transaction.replace(R.id.root_properties_bar, transformToolFragment, null);
                break;
            case Tools.CROP_TOOL:
                transaction.replace(R.id.root_properties_bar, cropToolFragment, null);
                break;
            case Tools.ZOOM_TOOL:
            default:
                transaction.replace(R.id.root_properties_bar, emptyToolFragment, null);
                break;
        }

        if (tool != Tools.TRANSFORMATION_TOOL) {
            TransformationView transformationView = requireActivity().findViewById(R.id.transformation_area_view);
            if (transformationView != null) {
                transformationView.cancelTransformation();
                transformationView.setVisibility(View.GONE);
            }
        }

        if (tool != Tools.CROP_TOOL) {
            CropView cropView = requireActivity().findViewById(R.id.crop_view);
            if (cropView != null) {
                cropView.setVisibility(View.GONE);
            }
        }

        transaction.commit();
    }

}
