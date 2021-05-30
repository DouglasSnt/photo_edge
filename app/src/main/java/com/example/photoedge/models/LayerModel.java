package com.example.photoedge.models;

import android.graphics.Bitmap;

import androidx.core.math.MathUtils;

import com.example.photoedge.gson.Exclude;
import com.example.photoedge.helpers.RandomString;

public class LayerModel {

    private String layerName;
    @Exclude
    private Bitmap bitmap;
    private int opacity;
    private boolean visibility;

    public static LayerModel create(Bitmap bm) {
        LayerModel layerModel = new LayerModel();
        layerModel.setLayerName(new RandomString(8).nextString());
        layerModel.setOpacity(100);
        layerModel.setVisibility(true);
        layerModel.setBitmap(bm);
        return layerModel;
    }

    public static LayerModel copy(LayerModel layerModel) {
        LayerModel model = new LayerModel();
        model.setLayerName(layerModel.getLayerName());
        model.setOpacity(layerModel.getOpacity());
        model.setVisibility(layerModel.visibility);
        model.setBitmap(Bitmap.createBitmap(layerModel.getBitmap()));
        return model;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }

    public boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void addOpacity() {
        setOpacity(MathUtils.clamp(getOpacity() + 5, 0, 100));
    }

    public void subOpacity() {
        setOpacity(MathUtils.clamp(getOpacity() - 5, 0, 100));
    }
}
