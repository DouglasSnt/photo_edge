package com.example.photoedge.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;
import androidx.fragment.app.Fragment;

import com.example.photoedge.models.LayerModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import java.util.List;

import static com.example.photoedge.helpers.LogHelper.mLog;

public class MyCanvas extends View {

    private List<LayerModel> layerModelList;
    private int specWidth = 0;
    private int specHeight = 0;
    private final Paint opacityPaint = new Paint();
    private final Rect renderArea = new Rect(0, 0, 0, 0);
    private final Rect viewBox = new Rect(0, 0, 0, 0);
    private float currentZoom = 1f;
    private final PointF translate = new PointF(0, 0);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (layerModelList == null) return;
        canvas.drawColor(Color.TRANSPARENT);

        for (int i = layerModelList.size(); i > 0; i--) {
            if (!layerModelList.get(i - 1).getVisibility()) continue;
            Bitmap bm = layerModelList.get(i - 1).getBitmap();
            opacityPaint.setAlpha((int) (255 * (layerModelList.get(i - 1).getOpacity() / 100f)));
            canvas.drawBitmap(bm, viewBox, renderArea, opacityPaint);
        }
    }

    public void setCanvasSize(int width, int height) {
        specWidth = width;
        specHeight = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);

        float wRatio = ((float) screenWidth / specWidth);
        float hRatio = ((float) screenHeight / specHeight);
        int w;
        int h;

        if (hRatio < wRatio) {
            h = (int) (specHeight * hRatio);
            w = (int) (specWidth * hRatio);
        } else {
            h = (int) (specHeight * wRatio);
            w = (int) (specWidth * wRatio);
        }

        int mWidth = MathUtils.clamp(w, 0, MeasureSpec.getSize(widthMeasureSpec));
        int mHeight = MathUtils.clamp(h, 0, MeasureSpec.getSize(heightMeasureSpec));
        renderArea.set(0, 0, (int) (w * currentZoom), (int) (h * currentZoom));

        float l = translate.x;
        float t = translate.y;
        float r = translate.x + specWidth;
        float b = translate.y + specHeight;

        viewBox.set((int) l, (int) t, (int) r, (int) b);

        Log.d("onMeasure", "RA: " + mLog(renderArea) + " VB: " + mLog(viewBox) + "Tr: " + mLog(translate) + " M: [" + (mWidth * currentZoom) + ", " + (mHeight * currentZoom) + "]" + " Z: " + currentZoom);

        setMeasuredDimension(mWidth, mHeight);
    }

    public MyCanvas(Context context) {
        super(context);
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProjectViewModel(Fragment fragment, ProjectViewModel projectViewModel) {

        projectViewModel.getProjectModel().observe(fragment.getViewLifecycleOwner(), model -> {
            currentZoom = model.getCurrentZoom();
            layerModelList = model.getLayerList();
            translate.set(model.getPreTranslate().x, model.getPreTranslate().y);
            requestLayout();
            invalidate();
        });
    }
}
