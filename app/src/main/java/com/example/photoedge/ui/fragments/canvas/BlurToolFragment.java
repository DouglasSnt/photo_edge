package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.ui.viewmodels.BlurToolViewModel;

public class BlurToolFragment extends Fragment {

    public BlurToolFragment() {
        super(R.layout.fragment_blur_properties);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BlurToolViewModel model = new ViewModelProvider(requireActivity()).get(BlurToolViewModel.class);

        TextView sizeTextView = view.findViewById(R.id.tool_size_tv);
        TextView strengthTextView = view.findViewById(R.id.tool_strength_tv);

        view.findViewById(R.id.add_btn).setOnClickListener((v) -> model.addSize());
        view.findViewById(R.id.less_btn).setOnClickListener((v) -> model.subtractSize());
        view.findViewById(R.id.add_strength_btn).setOnClickListener((v) -> model.addStrength());
        view.findViewById(R.id.less_strength_btn).setOnClickListener((v) -> model.subtractStrength());

        model.getStrength().observe(getViewLifecycleOwner(), value -> {
            String strength = Integer.toString(value.intValue());
            strengthTextView.setText(strength);
        });

        model.getSize().observe(getViewLifecycleOwner(), value -> {
            String size = Integer.toString(value);
            sizeTextView.setText(size);
        });

    }
}
