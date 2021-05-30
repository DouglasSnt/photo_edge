package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.ui.viewmodels.EraserToolViewModel;

public class EraserToolFragment extends Fragment {

    public EraserToolFragment() {
        super(R.layout.fragment_eraser_properties);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EraserToolViewModel model = new ViewModelProvider(requireActivity()).get(EraserToolViewModel.class);

        TextView textView = view.findViewById(R.id.tool_size_tv);

        view.findViewById(R.id.add_btn).setOnClickListener((v) -> model.addSize());

        view.findViewById(R.id.less_btn).setOnClickListener((v) -> model.subtractSize());

        model.getSize().observe(getViewLifecycleOwner(), integer -> {
            String size = Integer.toString(integer);
            textView.setText(size);
        });

    }

}
