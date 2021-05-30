package com.example.photoedge.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CropToolViewModel extends ViewModel {

    private final MutableLiveData<Boolean> keepRatio = new MutableLiveData<>();

    public LiveData<Boolean> getKeepRatio() {
        return keepRatio;
    }

    public void setKeepRatio(Boolean keepRatio) {
        this.keepRatio.setValue(keepRatio);
    }
}
