package com.example.photoedge.ui.viewmodels;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.math.MathUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoedge.models.ProjectModel;

import java.util.ArrayList;
import java.util.List;

public class BrushToolViewModel extends ViewModel {

    private final MutableLiveData<Integer> size = new MutableLiveData<>();
    private final MutableLiveData<Integer> color = new MutableLiveData<>();

    private static final int minSize = 1;
    private static final int maxSize = 300;
    private final int sizeInterval = 5;
    private final int initialSize = 30;
    private final Paint mPaint;
    private Bitmap bm;
    private Bitmap bmBackup;
    private Canvas mCanvas;
    private final Path drawPath = new Path();
    private final List<PointF> pointList = new ArrayList<>();

    public BrushToolViewModel() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(initialSize);
        mPaint.setAntiAlias(true);
        mPaint.setDither(false);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        setColor(255, 255, 0, 0);
        size.setValue(initialSize);
    }

    public LiveData<Integer> getSize() {
        if (this.size.getValue() == null) {
            this.size.setValue(initialSize);
        }
        return size;
    }

    public void addSize() {
        if (this.size.getValue() == null) {
            this.size.setValue(initialSize + sizeInterval);
        } else {
            int newValue;
            if (this.size.getValue() < 5) {
                newValue = (this.size.getValue() + 1);
            } else {
                newValue = (this.size.getValue() + sizeInterval);
                if (newValue % sizeInterval != 0) {
                    newValue = (int) (5 * (Math.round(newValue / 5.0)));
                }
            }
            newValue = MathUtils.clamp(newValue, minSize, maxSize);
            this.size.setValue(newValue);
        }
        mPaint.setStrokeWidth(this.size.getValue());
    }

    public void subtractSize() {
        if (this.size.getValue() == null) {
            this.size.setValue(initialSize - sizeInterval);
        } else {
            int newValue;
            if (this.size.getValue() <= 5) {
                newValue = (this.size.getValue() - 1);
            } else {
                newValue = (this.size.getValue() - sizeInterval);
                if (newValue % sizeInterval != 0) {
                    newValue = (int) (5 * (Math.round(newValue / 5.0)));
                }
            }
            newValue = MathUtils.clamp(newValue, minSize, maxSize);
            this.size.setValue(newValue);
        }
        mPaint.setStrokeWidth(this.size.getValue());
    }

    public void setColor(int a, int r, int g, int b) {
        this.color.setValue(Color.argb(a, r, g, b));
        this.mPaint.setColor(this.color.getValue());
    }

    public LiveData<Integer> getColor() {
        return this.color;
    }

    @SuppressLint("ClickableViewAccessibility")
    public View.OnTouchListener getToolAction(ProjectViewModel projectViewModel) {

        return (v, e) -> {
            ProjectModel projectModel = projectViewModel.getProjectModel().getValue();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    projectViewModel.addUndoStep(projectModel.getCurrentLayerModel());

                    bm = projectModel.getCurrentBitmap();
                    bmBackup = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);

                    mCanvas = new Canvas(bmBackup);
                    drawPath.reset();
                    pointList.clear();
                    float x = projectModel.getWidth() * (e.getX() / v.getWidth());
                    float y = projectModel.getHeight() * (e.getY() / v.getHeight());
                    drawPath.moveTo(x, y);
                    drawPath.lineTo(x, y);
                    pointList.add(new PointF(x, y));
                    Float zoom = projectModel.getCurrentZoom();
                    mPaint.setStrokeWidth(size.getValue() * zoom);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE: {
                    float x = projectModel.getWidth() * (e.getX() / v.getWidth());
                    float y = projectModel.getHeight() * (e.getY() / v.getHeight());
                    pointList.add(new PointF(x, y));

                    if (pointList.size() > 2) {
                        PointF p2 = pointList.get(pointList.size() - 2);
                        PointF p1 = pointList.get(pointList.size() - 1);
                        drawPath.quadTo(p2.x, p2.y, (p1.x + p2.x) / 2, (y + p2.y) / 2);
                    }

                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    Matrix matrix = new Matrix();
                    matrix.preTranslate(0, 0);
                    matrix.preScale(1, 1);
                    mCanvas.setMatrix(matrix);
                    mCanvas.drawBitmap(bm, 0, 0, null);

                    PointF tr = projectModel.getPreTranslate();
                    Float zoom = projectModel.getCurrentZoom();

                    matrix.preTranslate(tr.x, tr.y);
                    matrix.preScale(1f / zoom, 1f / zoom);
                    mCanvas.setMatrix(matrix);
                    mCanvas.drawPath(drawPath, mPaint);

                    projectModel.setBitmapToActiveLayer(bmBackup);
                    projectViewModel.invalidate();
                    break;
                }
                default:
            }

            return true;
        };
    }

}
