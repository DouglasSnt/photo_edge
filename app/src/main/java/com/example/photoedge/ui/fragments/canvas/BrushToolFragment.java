package com.example.photoedge.ui.fragments.canvas;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoedge.R;
import com.example.photoedge.ui.viewmodels.BrushToolViewModel;

import org.jetbrains.annotations.NotNull;

import codes.side.andcolorpicker.group.PickerGroup;
import codes.side.andcolorpicker.model.IntegerHSLColor;
import codes.side.andcolorpicker.view.picker.ColorSeekBar;

public class BrushToolFragment extends Fragment {

    public BrushToolFragment() {
        super(R.layout.fragment_brush_properties);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BrushToolViewModel model = new ViewModelProvider(requireActivity()).get(BrushToolViewModel.class);

        PickerGroup<IntegerHSLColor> group = new PickerGroup<>();
        group.registerPicker(view.findViewById(R.id.hue_color_bar));
        group.registerPicker(view.findViewById(R.id.saturation_color_bar));
        group.registerPicker(view.findViewById(R.id.lightness_color_bar));

        IntegerHSLColor hslColor = new IntegerHSLColor();

        float[] hsl = new float[3];
        Integer color = model.getColor().getValue();
        if (color != null) {
            Color.colorToHSV(color, hsl);
            hslColor.setFloatH(hsl[0]);
            hslColor.setFloatS(hsl[1]);
            hslColor.setFloatL(hsl[2]);
            group.setColor(hslColor);
            view.findViewById(R.id.selected_color_preview).setBackgroundTintList(ColorStateList.valueOf(color));
        }


        group.addListener(
                new ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor>() {
                    @Override
                    public void onColorChanged(@NotNull ColorSeekBar<IntegerHSLColor> picker, @NotNull IntegerHSLColor color, int value) {
                        super.onColorChanged(picker, color, value);
                        float[] hsl = new float[]{color.getFloatH(), color.getFloatS(), color.getFloatL()};
                        int c = Color.HSVToColor(hsl);
                        model.setColor(255, Color.red(c), Color.green(c), Color.blue(c));

                        view.findViewById(R.id.selected_color_preview).setBackgroundTintList(ColorStateList.valueOf(c));
                    }
                }
        );

        TextView textView = view.findViewById(R.id.tool_size_tv);

        view.findViewById(R.id.add_btn).setOnClickListener((v) -> model.addSize());
        view.findViewById(R.id.less_btn).setOnClickListener((v) -> model.subtractSize());

        model.getSize().observe(getViewLifecycleOwner(), integer -> {
            String size = Integer.toString(integer);
            textView.setText(size);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PickerGroup<IntegerHSLColor> group = new PickerGroup<>();
        group.clearListeners();
    }

}
