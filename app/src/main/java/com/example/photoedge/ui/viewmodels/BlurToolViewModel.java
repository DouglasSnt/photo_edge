package com.example.photoedge.ui.viewmodels;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.math.MathUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoedge.models.ProjectModel;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BlurToolViewModel extends ViewModel {

    private final MutableLiveData<Integer> size = new MutableLiveData<>();
    private final MutableLiveData<Double> strength = new MutableLiveData<>();

    private static final int minSize = 1;
    private static final int maxSize = 300;
    private final int sizeInterval = 5;
    private final int initialSize = 30;
    private static final double minStrength = 3;
    private static final double maxStrength = 301;
    private final double strengthInterval1 = 2;
    private final double strengthInterval2 = 4;
    private final double initialStrength = 45.0;
    private final Point lastPoint = new Point();
    private final Scalar color = new Scalar(255);
    private final Paint mPaint;

    public BlurToolViewModel() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(initialSize);
        mPaint.setAntiAlias(true);
        mPaint.setDither(false);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL));
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
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
            int newValue = (this.size.getValue() + sizeInterval);
            if (newValue % sizeInterval != 0) {
                newValue = (int) (5 * (Math.round(newValue / 5.0)));
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
            int newValue = (this.size.getValue() - sizeInterval);
            if (newValue % sizeInterval != 0) {
                newValue = (int) (5 * (Math.round(newValue / 5.0)));
            }
            newValue = MathUtils.clamp(newValue, minSize, maxSize);
            this.size.setValue(newValue);
        }
        mPaint.setStrokeWidth(this.size.getValue());
    }

    public LiveData<Double> getStrength() {
        if (this.strength.getValue() == null) {
            this.strength.setValue(initialStrength);
        }
        return this.strength;
    }

    public void addStrength() {
        if (this.strength.getValue() == null) {
            this.strength.setValue(initialStrength + strengthInterval1);
        } else {
            double newValue;
            if (strength.getValue() < 15)
                newValue = (this.strength.getValue() + strengthInterval1);
            else
                newValue = (this.strength.getValue() + strengthInterval2);

            if (newValue % 2.0 == 0.0) {
                newValue++;
            }
            newValue = MathUtils.clamp(newValue, minStrength, maxStrength);
            this.strength.setValue(newValue);
        }
    }

    public void subtractStrength() {
        if (this.strength.getValue() == null) {
            this.strength.setValue(initialStrength - strengthInterval1);
        } else {
            double newValue;
            if (strength.getValue() < 15)
                newValue = (this.strength.getValue() - strengthInterval1);
            else
                newValue = (this.strength.getValue() - strengthInterval2);

            if (newValue % 2.0 == 0.0) {
                newValue++;
            }
            newValue = MathUtils.clamp(newValue, minStrength, maxStrength);
            this.strength.setValue(newValue);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public View.OnTouchListener getToolAction(ProjectViewModel projectViewModel) {

        return (v, e) -> {
            ProjectModel projectModel = projectViewModel.getProjectModel().getValue();
            Bitmap bm = projectModel.getCurrentLayerModel().getBitmap();
            Integer size1 = getSize().getValue();
            Double strength1 = getStrength().getValue();
            if (bm == null || size1 == null || strength1 == null) return true;
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    projectViewModel.addUndoStep(projectModel.getCurrentLayerModel());

                    Float currentZoom = projectModel.getCurrentZoom();
                    Integer mWidth = projectModel.getWidth();
                    Integer mHeight = projectModel.getHeight();
                    PointF tr = projectModel.getPreTranslate();

                    float nWidth = mWidth / currentZoom;
                    float nHeight = mHeight / currentZoom;
                    float x = tr.x + nWidth * (e.getX() / v.getWidth());
                    float y = tr.y + nHeight * (e.getY() / v.getHeight());

                    double[] points = {x, y};
                    lastPoint.set(points);
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_MOVE: {
                    bm = projectModel.getCurrentLayerModel().getBitmap();
                    Mat mask = Mat.zeros(bm.getHeight(), bm.getWidth(), CvType.CV_8UC1);
                    Mat frame = new Mat();
                    Utils.bitmapToMat(bm, frame);
                    Mat temp = frame.clone();

                    Float currentZoom = projectModel.getCurrentZoom();
                    Integer mWidth = projectModel.getWidth();
                    Integer mHeight = projectModel.getHeight();
                    PointF tr = projectModel.getPreTranslate();

                    float nWidth = mWidth / currentZoom;
                    float nHeight = mHeight / currentZoom;
                    float x = tr.x + nWidth * (e.getX() / v.getWidth());
                    float y = tr.y + nHeight * (e.getY() / v.getHeight());

                    double[] points = {x, y};

                    Imgproc.line(mask, lastPoint, new Point(x, y), color, size1);
                    Imgproc.blur(temp, temp, new Size(strength1, strength1));
                    temp.copyTo(frame, mask);
                    Utils.matToBitmap(frame, bm);
                    temp.release();
                    mask.release();
                    frame.release();
                    lastPoint.set(points);
                    projectViewModel.invalidate();
                    break;
                }
                default:
            }

            return true;
        };
    }

}
