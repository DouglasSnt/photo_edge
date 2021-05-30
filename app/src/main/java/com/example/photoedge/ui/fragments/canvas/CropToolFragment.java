package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.enums.Enums;
import com.example.photoedge.ui.viewmodels.CropToolViewModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

public class CropToolFragment extends Fragment {

    public CropToolFragment() {
        super(R.layout.fragment_crop_properties);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        ProjectViewModel projectViewModel = new ViewModelProvider(activity).get(ProjectViewModel.class);
        CropToolViewModel cropToolViewModel = new ViewModelProvider(activity).get(CropToolViewModel.class);

        View confirmBtn = view.findViewById(R.id.confirm_transformation);
        View cancelBtn = view.findViewById(R.id.cancel_transformation);
        AppCompatImageButton ratioBtn = view.findViewById(R.id.keep_ratio_btn);

        cancelBtn.setOnClickListener(v -> projectViewModel.setTransformationState(Enums.TransformationState.CANCEL_CROP));
        confirmBtn.setOnClickListener(v -> projectViewModel.setTransformationState(Enums.TransformationState.CONFIRM_CROP));
        ratioBtn.setOnClickListener(v -> {
            Boolean ratio = cropToolViewModel.getKeepRatio().getValue();
            ratio = ratio != null && !ratio;
            if (ratio) ratioBtn.setImageResource(R.drawable.ic_ratio);
            else ratioBtn.setImageResource(R.drawable.ic_crop_free);
            cropToolViewModel.setKeepRatio(ratio);
        });

        projectViewModel.getProjectModel().getValue().setCurrentZoom(1f);
        projectViewModel.invalidate();
    }

}
