package com.example.photoedge.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ProjectModel {

    private String projectName;
    private String creationDate;
    private int width;
    private int height;
    private Float currentZoom;
    private PointF preTranslate;
    private List<LayerModel> layerList;
    private int currentLayer;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<LayerModel> getLayerList() {
        return layerList;
    }

    public void addLayer() {
        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        addLayer(bmp, false);
    }

    public void addLayer(Bitmap bmp, boolean crop) {
        LayerModel layerModel;
        if (crop) {
            Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);

            float importedWidth = bmp.getWidth();
            float importedHeight = bmp.getHeight();


            float scale = (importedHeight / height < importedWidth / width) ?
                    height / importedHeight : width / importedWidth;

            Matrix transformation = new Matrix();
            transformation.preTranslate((width - importedWidth * scale) / 2f, (height - importedHeight * scale) / 2f);
            transformation.preScale(scale, scale);

            Paint paint = new Paint();
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bmp, transformation, paint);
            layerModel = LayerModel.create(bm);
        } else {
            layerModel = LayerModel.create(bmp);
        }

        if (layerList == null) layerList = new ArrayList<>();
        layerList.add(layerModel);
        setCurrentLayer(layerList.size() - 1);
    }

    public Float getCurrentZoom() {
        return currentZoom;
    }

    public void setCurrentZoom(Float currentZoom) {
        this.currentZoom = currentZoom;
    }

    public PointF getPreTranslate() {
        return preTranslate;
    }

    public void setPreTranslate(PointF preTranslate) {
        this.preTranslate = preTranslate;
    }

    public int getCurrentLayer() {
        if (currentLayer > layerList.size() - 1)
            currentLayer = layerList.size() - 1;
        return currentLayer;
    }

    public void setCurrentLayer(int layer) {
        this.currentLayer = layer;
    }

    public LayerModel getCurrentLayerModel() {
        return getLayerList().get(getCurrentLayer());
    }

    public void layerToUp(int position) {
        if (getLayerList().size() <= 1 || position == 0) return;
        toggleLayers(position, -1);
        setCurrentLayer(getCurrentLayer() - 1);
    }

    public void layerToDown(int position) {
        if (getLayerList().size() - 1 == position) return;
        toggleLayers(position, 1);
        setCurrentLayer(getCurrentLayer() + 1);
    }

    private void toggleLayers(int position, int offset) {
        List<LayerModel> layerList = getLayerList();
        LayerModel aux = layerList.get(position + offset);
        layerList.set(position + offset, layerList.get(position));
        layerList.set(position, aux);
    }

    public void deleteLayer(int position) {
        List<LayerModel> layerList = getLayerList();

        if (layerList.size() <= 1) return;
        layerList.remove(position);
        if (getCurrentLayer() >= layerList.size()) setCurrentLayer(layerList.size() - 1);
    }

    public void setBitmapToActiveLayer(Bitmap bm) {
        getLayerList().get(getCurrentLayer()).setBitmap(bm);
    }

    public Bitmap getCurrentBitmap() {
        return getLayerList().get(getCurrentLayer()).getBitmap();
    }

}
