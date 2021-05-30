package com.example.photoedge.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

public class ImageHelper {

    public void importImageAsLayer(Bitmap importedBitmap, ProjectViewModel projectViewModel) {
        ProjectModel projectModel = projectViewModel.getProjectModel().getValue();
        int mWidth = projectModel.getWidth();
        int mHeight = projectModel.getHeight();

        Bitmap bm = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);

        float importedWidth = importedBitmap.getWidth();
        float importedHeight = importedBitmap.getHeight();

        float scale = (importedHeight / mHeight > importedWidth / mWidth) ?
                mHeight / importedHeight : mWidth / importedWidth;

        Matrix transformation = new Matrix();
        transformation.preTranslate((mWidth - importedWidth * scale) / 2f, (mHeight - importedHeight * scale) / 2f);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(importedBitmap, transformation, paint);

        projectModel.addLayer(bm, false);
        projectViewModel.invalidate();
    }

}
