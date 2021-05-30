package com.example.photoedge.ui.viewmodels;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoedge.enums.Enums;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.models.ProjectModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectViewModel extends ViewModel {

    private final MutableLiveData<ProjectModel> projectModel = new MutableLiveData<>();
    private final MutableLiveData<View.OnTouchListener> action = new MutableLiveData<>();
    private final MutableLiveData<Enums.TransformationState> transformationState = new MutableLiveData<>();
    private final MutableLiveData<Integer> activeTool = new MutableLiveData<>();
    private final List<LayerModel> undoList = new ArrayList<>();

    public void setActiveTool(int tool) {
        if (!Integer.valueOf(tool).equals(activeTool.getValue()))
            activeTool.setValue(tool);
    }


    public LiveData<Integer> getActiveTool() {
        return activeTool;
    }

    public void setAction(View.OnTouchListener listener) {
        this.action.setValue(listener);
    }

    public LiveData<View.OnTouchListener> getAction() {
        return this.action;
    }

    public void setTransformationState(Enums.TransformationState state) {
        this.transformationState.setValue(state);
    }

    public MutableLiveData<Enums.TransformationState> getTransformationState() {
        return this.transformationState;
    }

    public MutableLiveData<ProjectModel> getProjectModel() {
        return projectModel;
    }

    public void invalidate() {
        projectModel.setValue(projectModel.getValue());
    }

    public void addUndoStep(LayerModel layerModel) {
        LayerModel copy = LayerModel.copy(layerModel);
        undoList.add(copy);
        if (undoList.size() > 20) {
            undoList.get(0).getBitmap().recycle();
            undoList.remove(0);
        }
    }

    public void undo() {
        List<LayerModel> layerList = projectModel.getValue().getLayerList();

        if (!undoList.isEmpty()) {
            LayerModel toUndo = undoList.get(undoList.size() - 1);

            for (int i = 0; i < layerList.size(); i++) {
                if (toUndo.getLayerName().equals(layerList.get(i).getLayerName())) {
                    layerList.get(i).getBitmap().recycle();
                    layerList.set(i, toUndo);
                    break;
                }
            }
            undoList.remove(undoList.size() - 1);
            invalidate();
        }
    }

    public void clearUndoList() {
        while (!undoList.isEmpty()) {
            undoList.get(0).getBitmap().recycle();
            undoList.remove(0);
        }
    }
}
