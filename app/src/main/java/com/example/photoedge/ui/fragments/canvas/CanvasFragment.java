package com.example.photoedge.ui.fragments.canvas;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.customview.MyCanvas;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

public class CanvasFragment extends Fragment {

    public CanvasFragment() {
        super(R.layout.fragment_canvas);
    }


    private MyCanvas mCanvas;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        mCanvas = activity.findViewById(R.id.canvas_view);

        ProjectViewModel model = new ViewModelProvider(activity).get(ProjectViewModel.class);
        model.getAction().observe(getViewLifecycleOwner(), listener -> mCanvas.setOnTouchListener(listener));

        ProjectModel projectModel = model.getProjectModel().getValue();
        int frameWidth = projectModel.getWidth();
        int frameHeight = projectModel.getHeight();

        mCanvas.setCanvasSize(frameWidth, frameHeight);
        mCanvas.setProjectViewModel(this, model);

    }

}
