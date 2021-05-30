package com.example.photoedge.ui.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import java.util.List;

public class LayersAdapter extends RecyclerView.Adapter<LayerItemViewHolder> {

    List<LayerModel> layerItemList;
    ProjectViewModel projectViewModel;
    Activity activity;

    public LayersAdapter(Activity activity, ProjectViewModel projectViewModel) {
        this.projectViewModel = projectViewModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LayerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layer_item_pattern, parent, false);
        return new LayerItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull LayerItemViewHolder holder, int position) {
        holder.setInfoOnView(activity, layerItemList.get(position), projectViewModel, position);
    }

    @Override
    public int getItemCount() {
        if (layerItemList == null) return 0;
        return layerItemList.size();
    }

    public void setLayerModelList(List<LayerModel> layerItemList) {
        this.layerItemList = layerItemList;
    }

}
