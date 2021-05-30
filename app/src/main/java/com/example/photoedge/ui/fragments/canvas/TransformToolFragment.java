package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.enums.Enums;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

public class TransformToolFragment extends Fragment {

    public TransformToolFragment() {
        super(R.layout.fragment_transformation_properties);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProjectViewModel projectViewModel = new ViewModelProvider(requireActivity()).get(ProjectViewModel.class);

        View confirmBtn = view.findViewById(R.id.confirm_transformation);
        View cancelBtn = view.findViewById(R.id.cancel_transformation);

        cancelBtn.setOnClickListener(v -> projectViewModel.setTransformationState(Enums.TransformationState.CANCEL_TRANSFORMATION));
        confirmBtn.setOnClickListener(v -> projectViewModel.setTransformationState(Enums.TransformationState.CONFIRM_TRANSFORMATION));

        projectViewModel.getProjectModel().getValue().setCurrentZoom(1f);
        projectViewModel.invalidate();
    }

}
