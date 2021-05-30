package com.example.photoedge.ui.list;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

public class LayerItemViewHolder extends RecyclerView.ViewHolder {

    View view;

    public LayerItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setInfoOnView(Activity activity, LayerModel layerModel, ProjectViewModel projectViewModel, int position) {
        View thumbPreviewBorder = view.findViewById(R.id.thumb_preview_border);
        ImageView thumbPreview = view.findViewById(R.id.layer_thumb_preview);
        thumbPreview.setImageBitmap(layerModel.getBitmap());

        AppCompatImageButton visibilityBtn = view.findViewById(R.id.layer_visibility_btn);
        AppCompatImageButton addOpacityBtn = view.findViewById(R.id.add_btn);
        AppCompatImageButton subOpacityBtn = view.findViewById(R.id.less_btn);
        AppCompatImageButton layerToUpBtn = view.findViewById(R.id.layer_to_up);
        AppCompatImageButton layerToDownBtn = view.findViewById(R.id.layer_to_down);
        AppCompatImageButton deleteLayerBtn = view.findViewById(R.id.delete_layer_btn);
        TextView opacityTv = view.findViewById(R.id.layer_opacity_tv);

        if (layerModel.getVisibility()) visibilityBtn.setImageResource(R.drawable.ic_visibility_on);
        else visibilityBtn.setImageResource(R.drawable.ic_visibility_off);

        String s = layerModel.getOpacity() + "%";
        opacityTv.setText(s);

        ProjectModel projectModel = projectViewModel.getProjectModel().getValue();

        if (position == projectModel.getCurrentLayer()) {
            thumbPreviewBorder.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.primary_blue));
        } else {
            thumbPreviewBorder.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.white));
        }

        thumbPreview.setOnClickListener(v -> {
            projectModel.setCurrentLayer(position);
            projectViewModel.invalidate();
        });

        layerToUpBtn.setOnClickListener(v -> {
            projectModel.layerToUp(position);
            projectViewModel.invalidate();
        });

        layerToDownBtn.setOnClickListener(v -> {
            projectModel.layerToDown(position);
            projectViewModel.invalidate();
        });

        deleteLayerBtn.setOnClickListener(v -> {
            projectModel.deleteLayer(position);
            projectViewModel.invalidate();
        });

        addOpacityBtn.setOnClickListener(v -> {
            layerModel.addOpacity();
            projectViewModel.invalidate();
        });

        subOpacityBtn.setOnClickListener(v -> {
            layerModel.subOpacity();
            projectViewModel.invalidate();
        });

        visibilityBtn.setOnClickListener(v -> {
            layerModel.setVisibility(!layerModel.getVisibility());
            projectViewModel.invalidate();
        });
    }

}
