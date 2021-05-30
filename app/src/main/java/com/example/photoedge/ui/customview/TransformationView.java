package com.example.photoedge.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.fragment.app.Fragment;

import com.example.photoedge.R;
import com.example.photoedge.enums.Enums;
import com.example.photoedge.helpers.DimensHelper;
import com.example.photoedge.models.LayerModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransformationView extends FrameLayout {

    private ImageView point1;
    private ImageView point2;
    private ImageView point3;
    private ImageView point4;
    private ImageView point5;
    private final Path path = new Path();
    private final PointF pP1P20 = new PointF();
    private final PointF pP1P21 = new PointF();
    private final PointF pP3P40 = new PointF();
    private final PointF pP3P41 = new PointF();
    private final PointF pP1P30 = new PointF();
    private final PointF pP1P31 = new PointF();
    private final PointF pP2P40 = new PointF();
    private final PointF pP2P41 = new PointF();
    private float[] dstArrowMatrix;
    private final Matrix arrowMatrix = new Matrix();
    private final Paint backgroundPaint = new Paint();
    private final Paint extensionLinePaint = new Paint();
    private final Paint outLinePaint = new Paint();
    private final Paint perspecPaint = new Paint();
    private Context context;
    private Canvas mCanvas;
    private Bitmap backupBm;
    private Bitmap editBm;
    private int mWidth = 0;
    private int mHeight = 0;

    public List<PointF> getPoints() {
        List<PointF> p = new ArrayList<>();

        p.add(new PointF(point1.getX() + point1.getWidth() / 2f, point1.getY() + point1.getHeight() / 2f));
        p.add(new PointF(point2.getX() + point2.getWidth() / 2f, point2.getY() + point2.getHeight() / 2f));
        p.add(new PointF(point3.getX() + point3.getWidth() / 2f, point3.getY() + point3.getHeight() / 2f));
        p.add(new PointF(point4.getX() + point4.getWidth() / 2f, point4.getY() + point4.getHeight() / 2f));

        return p;
    }

    public TransformationView(@NonNull @NotNull Context context) {
        super(context);
        init(context);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        this.context = context;
        point1 = addPoint();
        point2 = addPoint();
        point3 = addPoint();
        point4 = addPoint();
        point5 = addPoint();

        point1.setOnTouchListener(new PointTouchListener());
        point2.setOnTouchListener(new PointTouchListener());
        point3.setOnTouchListener(new PointTouchListener());
        point4.setOnTouchListener(new PointTouchListener());
        point5.setOnTouchListener(new CenterTouchListener());

        addView(point1);
        addView(point2);
        addView(point3);
        addView(point4);
        addView(point5);

        Paint midLinePaint = new Paint();
        midLinePaint.setColor(ContextCompat.getColor(context, R.color.white));
        midLinePaint.setStrokeWidth(2f);
        midLinePaint.setAntiAlias(true);

        outLinePaint.setColor(ContextCompat.getColor(context, R.color.black));
        outLinePaint.setStrokeWidth(5f);
        outLinePaint.setAntiAlias(true);

        extensionLinePaint.setColor(ContextCompat.getColor(context, R.color.white));
        extensionLinePaint.setStrokeWidth(2f);
        extensionLinePaint.setAntiAlias(true);

        backgroundPaint.setColor(Color.parseColor("#20000000"));
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private ImageView addPoint() {
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.point_size),
                getResources().getDimensionPixelSize(R.dimen.point_size)
        );
        int p = (int) DimensHelper.dpToPx(15f, context);
        imageView.setPadding(p, p, p, p);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.drag_transformation_point);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }

    public List<PointF> getPointsRelative() {
        List<PointF> p = new ArrayList<>();

        p.add(
                new PointF(
                        (point1.getX() + point1.getWidth() / 2f) / getWidth() * 100f,
                        (point1.getY() + point1.getHeight() / 2f) / getHeight() * 100f
                )
        );
        p.add(
                new PointF(
                        (point2.getX() + point2.getWidth() / 2f) / getWidth() * 100f,
                        (point2.getY() + point2.getHeight() / 2f) / getHeight() * 100f
                )
        );
        p.add(
                new PointF(
                        (point3.getX() + point1.getWidth() / 2f) / getWidth() * 100f,
                        (point3.getY() + point1.getHeight() / 2f) / getHeight() * 100f
                )
        );
        p.add(
                new PointF(
                        (point4.getX() + point1.getWidth() / 2f) / getWidth() * 100f,
                        (point4.getY() + point1.getHeight() / 2f) / getHeight() * 100f
                )
        );

        return p;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetPoints(w, h);
    }

    private void resetPoints(int w, int h) {
        int size = getResources().getDimensionPixelSize(R.dimen.point_size);

        point1.setX(-size / 2f);
        point1.setY(-size / 2f);

        point2.setX(w - size / 2f);
        point2.setY(-size / 2f);

        point3.setX(-size / 2f);
        point3.setY(h - size / 2f);

        point4.setX(w - size / 2f);
        point4.setY(h - size / 2f);

        updateCentroid();
        invalidate();
    }

    private void updateCentroid() {
        PointF c = centroid();
        point5.setX(c.x);
        point5.setY(c.y);
    }

    public PointF centroid() {
        return new PointF(
                (point1.getX() + point2.getX() + point3.getX() + point4.getX()) / 4f,
                (point1.getY() + point2.getY() + point3.getY() + point4.getY()) / 4f
        );
    }

    public Boolean isValidShape() {
        List<PointF> p = getPoints();

        //  1 2  |  0 1
        //  3 4  |  2 3

        double d12 = Math.sqrt(Math.pow(p.get(0).x - p.get(1).x, 2) + Math.pow(p.get(0).y - p.get(1).y, 2));
        double d24 = Math.sqrt(Math.pow(p.get(1).x - p.get(3).x, 2) + Math.pow(p.get(1).y - p.get(3).y, 2));
        double d34 = Math.sqrt(Math.pow(p.get(3).x - p.get(2).x, 2) + Math.pow(p.get(3).y - p.get(2).y, 2));
        double d13 = Math.sqrt(Math.pow(p.get(2).x - p.get(0).x, 2) + Math.pow(p.get(2).y - p.get(0).y, 2));
        double d23 = Math.sqrt(Math.pow(p.get(1).x - p.get(2).x, 2) + Math.pow(p.get(1).y - p.get(2).y, 2));
        double d14 = Math.sqrt(Math.pow(p.get(0).x - p.get(3).x, 2) + Math.pow(p.get(0).y - p.get(3).y, 2));

        double arc213 = (Math.pow(d23, 2) - Math.pow(d12, 2) - Math.pow(d13, 2)) / (-2 * d12 * d13);
        double arc124 = (Math.pow(d14, 2) - Math.pow(d12, 2) - Math.pow(d24, 2)) / (-2 * d12 * d24);
        double arc243 = (Math.pow(d23, 2) - Math.pow(d34, 2) - Math.pow(d24, 2)) / (-2 * d34 * d24);
        double arc134 = (Math.pow(d14, 2) - Math.pow(d13, 2) - Math.pow(d34, 2)) / (-2 * d13 * d34);

        double ang213 = Math.acos(arc213) * 180 / Math.PI;
        double ang124 = Math.acos(arc124) * 180 / Math.PI;
        double ang243 = Math.acos(arc243) * 180 / Math.PI;
        double ang134 = Math.acos(arc134) * 180 / Math.PI;

        double ang = ang213 + ang124 + ang243 + ang134;

        int minDimen = (getWidth() < getHeight()) ? getWidth() : getHeight();

        boolean isValid;
        float minAngle = 5f;
        float minDistance = 1f;
        if (ang < 359.9 ||
                ang213 < minAngle || ang124 < minAngle || ang243 < minAngle || ang134 < minAngle
                || (d12 / minDimen) * 100f < minDistance || (d24 / minDimen) * 100f < minDistance
                || (d34 / minDimen) * 100f < minDistance || (d13 / minDimen) * 100f < minDistance
        ) {
            outLinePaint.setColor(ContextCompat.getColor(context, R.color.primary_red));
            isValid = false;
        } else {
            outLinePaint.setColor(ContextCompat.getColor(context, R.color.black));
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        mDraw(canvas);
        super.dispatchDraw(canvas);
    }

    private void mDraw(Canvas canvas) {
        List<PointF> p = getPoints();

        float refX0 = 0f;
        float refX1 = getWidth();
        float refY0 = 0f;
        float refY1 = getHeight();

        float extP1P20 = lineFunY(refX0, p.get(0), p.get(1));
        float extP1P21 = lineFunY(refX1, p.get(0), p.get(1));
        pP1P20.set(refX0, (extP1P20 * -1f));
        pP1P21.set(refX1, (extP1P21 * -1f));

        float extP3P40 = lineFunY(refX0, p.get(2), p.get(3));
        float extP3P41 = lineFunY(refX1, p.get(2), p.get(3));
        pP3P40.set(refX0, (extP3P40 * -1f));
        pP3P41.set(refX1, (extP3P41 * -1f));

        float extP1P30 = lineFunX(refY0, p.get(0), p.get(2));
        float extP1P31 = lineFunX(refY1, p.get(0), p.get(2));
        pP1P30.set(extP1P30, refY0);
        pP1P31.set(extP1P31, refY1);

        float extP2P40 = lineFunX(refY0, p.get(1), p.get(3));
        float extP2P41 = lineFunX(refY1, p.get(1), p.get(3));
        pP2P40.set(extP2P40, refY0);
        pP2P41.set(extP2P41, refY1);

        path.moveTo(point1.getX() + point1.getWidth() / 2f, point1.getY() + point1.getHeight() / 2f);
        path.lineTo(point2.getX() + point2.getWidth() / 2f, point2.getY() + point2.getHeight() / 2f);
        path.lineTo(point4.getX() + point4.getWidth() / 2f, point4.getY() + point4.getHeight() / 2f);
        path.lineTo(point3.getX() + point3.getWidth() / 2f, point3.getY() + point3.getHeight() / 2f);
        path.lineTo(point1.getX() + point1.getWidth() / 2f, point1.getY() + point1.getHeight() / 2f);

        canvas.drawPath(path, backgroundPaint);
        path.reset();

        drawLine(canvas, point1, point2, outLinePaint);
        drawLine(canvas, point1, point3, outLinePaint);
        drawLine(canvas, point2, point4, outLinePaint);
        drawLine(canvas, point3, point4, outLinePaint);

        canvas.drawLine(pP1P20.x, pP1P20.y, pP1P21.x, pP1P21.y, extensionLinePaint);
        canvas.drawLine(pP3P40.x, pP3P40.y, pP3P41.x, pP3P41.y, extensionLinePaint);
        canvas.drawLine(pP1P30.x, pP1P30.y, pP1P31.x, pP1P31.y, extensionLinePaint);
        canvas.drawLine(pP2P40.x, pP2P40.y, pP2P41.x, pP2P41.y, extensionLinePaint);

        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (isValidShape()) {

            List<PointF> pr = getPointsRelative();
            @SuppressLint("DrawAllocation")
            float[] src = new float[]{
                    (pr.get(0).x / 100f) * mWidth, (pr.get(0).y / 100) * mHeight,
                    (pr.get(1).x / 100f) * mWidth, (pr.get(1).y / 100) * mHeight,
                    (pr.get(3).x / 100f) * mWidth, (pr.get(3).y / 100) * mHeight,
                    (pr.get(2).x / 100f) * mWidth, (pr.get(2).y / 100) * mHeight,
            };

            arrowMatrix.setPolyToPoly(dstArrowMatrix, 0, src, 0, dstArrowMatrix.length >> 1);

            if (editBm != null) {
//                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mCanvas.drawBitmap(backupBm, arrowMatrix, perspecPaint);
            }
        }
    }


    private void drawLine(Canvas canvas, ImageView p1, ImageView p2, Paint p) {
        canvas.drawLine(
                p1.getX() + p1.getWidth() / 2f,
                p1.getY() + p1.getHeight() / 2f,
                p2.getX() + p2.getWidth() / 2f,
                p2.getY() + p2.getHeight() / 2f,
                p
        );
    }

    private float lineFunX(float y, PointF p1, PointF p2) {
        return (-(p1.x - p2.x) * -y - p1.x * p2.y + p2.x * p1.y) / (p1.y - p2.y);
    }

    private float lineFunY(float x, PointF p1, PointF p2) {
        return ((-(p1.x * p2.y) + (p2.x * p1.y) - (p1.y - p2.y) * x) / (p1.x - p2.x));
    }

    public void setImage(Bitmap bm) {
        editBm = bm;

        mWidth = bm.getWidth();
        mHeight = bm.getHeight();

        dstArrowMatrix = new float[]{
                0f, 0f,
                mWidth, 0f,
                mWidth, mHeight,
                0f, editBm.getHeight()
        };

        backupBm = Bitmap.createBitmap(editBm);
        mCanvas = new Canvas(editBm);
    }

    public TransformationView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TransformationView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setNewSelection(ProjectViewModel projectViewModel, Fragment activity) {
        projectViewModel.getTransformationState().observe(activity.getViewLifecycleOwner(), v -> {
            if (v == Enums.TransformationState.CANCEL_TRANSFORMATION) {
                cancelTransformation();
            }

            if (v == Enums.TransformationState.CONFIRM_TRANSFORMATION) {
                LayerModel copy = LayerModel.copy(projectViewModel.getProjectModel().getValue().getCurrentLayerModel());
                copy.setBitmap(Bitmap.createBitmap(backupBm));
                projectViewModel.addUndoStep(copy);
                setImage(projectViewModel.getProjectModel().getValue().getCurrentBitmap());
                resetPoints(getWidth(), getHeight());
            }
        });

        setImage(projectViewModel.getProjectModel().getValue().getCurrentBitmap());
    }

    public void cancelTransformation() {
        resetPoints(getWidth(), getHeight());
        if (mCanvas != null) {
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mCanvas.drawBitmap(backupBm, 0, 0, null);
        }
    }

    private class PointTouchListener implements OnTouchListener {
        PointF downPt = new PointF();
        PointF startPt = new PointF();

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE: {
                    PointF mv = new PointF(event.getX() - downPt.x, event.getY() - downPt.y);
                    float posX = (startPt.x + mv.x);
                    float posY = (startPt.y + mv.y);

                    posX = MathUtils.clamp(
                            posX,
                            (-v.getWidth() / 2f),
                            getWidth() - (v.getWidth() / 2f)
                    );
                    posY = MathUtils.clamp(
                            posY,
                            (-v.getHeight() / 2f),
                            getHeight() - (v.getHeight() / 2f)
                    );

                    v.setX(posX);
                    v.setY(posY);
                    updateCentroid();
                    startPt = new PointF(posX, posY);
                    break;
                }

                case MotionEvent.ACTION_DOWN: {
                    downPt.x = event.getX();
                    downPt.y = event.getY();
                    startPt = new PointF(v.getX(), v.getY());
                    break;
                }
                default:
                    break;
            }
            invalidate();
            return true;
        }
    }

    private class CenterTouchListener implements OnTouchListener {
        PointF downPt = new PointF();
        PointF startPt = new PointF();

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    PointF mv = new PointF(event.getX() - downPt.x, event.getY() - downPt.y);
                    float posX = (startPt.x + mv.x);
                    float posY = (startPt.y + mv.y);

                    posX = MathUtils.clamp(
                            posX,
                            (-v.getWidth() / 2f),
                            getWidth() - (v.getWidth() / 2f)
                    );
                    posY = MathUtils.clamp(
                            posY,
                            (-v.getHeight() / 2f),
                            getHeight() - (v.getHeight() / 2f)
                    );

                    v.setX(posX);
                    v.setY(posY);
                    startPt = new PointF(posX, posY);

                    updatePoint(point1, mv);
                    updatePoint(point2, mv);
                    updatePoint(point3, mv);
                    updatePoint(point4, mv);

                    updateCentroid();

                    break;
                }

                case MotionEvent.ACTION_DOWN: {
                    downPt.x = event.getX();
                    downPt.y = event.getY();
                    startPt = new PointF(v.getX(), v.getY());
                    break;
                }
                default:
                    break;
            }
            invalidate();
            return true;
        }

        private void updatePoint(View v, PointF p) {
            v.setX(v.getX() + p.x);
            v.setY(v.getY() + p.y);
        }
    }

}
