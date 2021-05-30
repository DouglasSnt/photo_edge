package com.example.photoedge.ui.fragments.canvas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.ui.list.LayersAdapter;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import java.util.ArrayList;
import java.util.List;

public class LayerToolFragment extends Fragment {

    public LayerToolFragment() {
        super(R.layout.fragment_layer_properties);
    }

    private LayersAdapter layersAdapter;


    public List<LayerModel> layerItemList = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity activity = requireActivity();

        RecyclerView layersRv = activity.findViewById(R.id.layer_list_recycler_view);
        AppCompatImageButton addLayerBtn = view.findViewById(R.id.add_layer_btn);

        ProjectViewModel model = new ViewModelProvider(activity).get(ProjectViewModel.class);
        model.getProjectModel().observe(getViewLifecycleOwner(), ignore -> updateLayerItemList(model));

        layersAdapter = new LayersAdapter(activity, model);
        layersAdapter.setLayerModelList(layerItemList);
        layersRv.setLayoutManager(new LinearLayoutManager(activity));
        layersRv.setAdapter(layersAdapter);

        addLayerBtn.setOnClickListener(v -> {
            model.getProjectModel().getValue().addLayer();
            model.invalidate();
        });
    }

    private void updateLayerItemList(ProjectViewModel model) {
        layerItemList.clear();
        layersAdapter.setLayerModelList(model.getProjectModel().getValue().getLayerList());
        layersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
