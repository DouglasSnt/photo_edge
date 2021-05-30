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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.fragment.app.Fragment;

import com.example.photoedge.R;
import com.example.photoedge.enums.Enums;
import com.example.photoedge.helpers.DimensHelper;
import com.example.photoedge.ui.viewmodels.CropToolViewModel;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CropView extends FrameLayout {

    private ImageView point1;
    private ImageView point2;
    private ImageView point3;
    private ImageView point4;
    private ImageView midLine1;
    private ImageView midLine2;
    private ImageView midLine3;
    private ImageView midLine4;
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
    private final Paint backgroundPaint = new Paint();
    private final Paint extensionLinePaint = new Paint();
    private final Paint outLinePaint = new Paint();
    private final Paint midLinePaint = new Paint();
    private Context context;
    private Canvas mCanvas;
    private Bitmap editBm;
    private int pointSize;
    private boolean keepRatio = true;

    public List<PointF> getPoints() {
        List<PointF> p = new ArrayList<>();

        p.add(new PointF(point1.getX() + point1.getWidth() / 2f, point1.getY() + point1.getHeight() / 2f));
        p.add(new PointF(point2.getX() + point2.getWidth() / 2f, point2.getY() + point2.getHeight() / 2f));
        p.add(new PointF(point3.getX() + point3.getWidth() / 2f, point3.getY() + point3.getHeight() / 2f));
        p.add(new PointF(point4.getX() + point4.getWidth() / 2f, point4.getY() + point4.getHeight() / 2f));

        return p;
    }

    public CropView(@NonNull @NotNull Context context) {
        super(context);
        init(context);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        this.context = context;

        pointSize = getResources().getDimensionPixelSize(R.dimen.point_size);

        point1 = addPoint(R.drawable.ic_crop_vertex_tl);
        point2 = addPoint(R.drawable.ic_crop_vertex_tr);
        point3 = addPoint(R.drawable.ic_crop_vertex_bl);
        point4 = addPoint(R.drawable.ic_crop_vertex_br);
        point5 = addPoint(R.drawable.ic_move);
        midLine1 = addMidLine(R.drawable.ic_line_left);
        midLine2 = addMidLine(R.drawable.ic_line_top);
        midLine3 = addMidLine(R.drawable.ic_line_right);
        midLine4 = addMidLine(R.drawable.ic_line_bottom);

        point1.setOnTouchListener(new PointTouchListener(1));
        point2.setOnTouchListener(new PointTouchListener(2));
        point3.setOnTouchListener(new PointTouchListener(3));
        point4.setOnTouchListener(new PointTouchListener(4));
        point5.setOnTouchListener(new CenterTouchListener());

        midLine1.setOnTouchListener(new MidLineTouchListener(1));
        midLine2.setOnTouchListener(new MidLineTouchListener(2));
        midLine3.setOnTouchListener(new MidLineTouchListener(3));
        midLine4.setOnTouchListener(new MidLineTouchListener(4));

        addView(point5);
        addView(point1);
        addView(point2);
        addView(point3);
        addView(point4);
        addView(midLine1);
        addView(midLine2);
        addView(midLine3);
        addView(midLine4);

        outLinePaint.setColor(ContextCompat.getColor(context, R.color.black));
        outLinePaint.setStrokeWidth(DimensHelper.dpToPx(3f, context));
        outLinePaint.setAntiAlias(true);

        midLinePaint.setColor(ContextCompat.getColor(context, R.color.white));
        midLinePaint.setStrokeWidth(DimensHelper.dpToPx(2f, context));
        midLinePaint.setAntiAlias(true);

        extensionLinePaint.setColor(ContextCompat.getColor(context, R.color.white));
        extensionLinePaint.setStrokeWidth(DimensHelper.dpToPx(.5f, context));
        extensionLinePaint.setAntiAlias(true);

        backgroundPaint.setColor(Color.parseColor("#20000000"));
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
    }

    @SuppressLint({"ClickableViewAccessibility", "InflateParams"})
    private ImageView addPoint(@DrawableRes int icon) {
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(pointSize, pointSize);
        int p = (int) DimensHelper.dpToPx(15f, context);
        imageView.setPadding(p, p, p, p);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(icon);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }

    private ImageView addMidLine(@DrawableRes int icon) {
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(pointSize, pointSize);
        int p = (int) DimensHelper.dpToPx(15f, context);
        imageView.setPadding(p, p, p, p);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(icon);
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
        float size = pointSize / 2f;

        point1.setX(-size);
        point1.setY(-size);

        point2.setX(w - size);
        point2.setY(-size);

        point3.setX(-size);
        point3.setY(h - size);

        point4.setX(w - size);
        point4.setY(h - size);

        updateCentroid();
        updateMidLines();
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

        drawLine(canvas, point1, point2, midLinePaint);
        drawLine(canvas, point1, point3, midLinePaint);
        drawLine(canvas, point2, point4, midLinePaint);
        drawLine(canvas, point3, point4, midLinePaint);

        canvas.drawLine(pP1P20.x, pP1P20.y, pP1P21.x, pP1P21.y, extensionLinePaint);
        canvas.drawLine(pP3P40.x, pP3P40.y, pP3P41.x, pP3P41.y, extensionLinePaint);
        canvas.drawLine(pP1P30.x, pP1P30.y, pP1P31.x, pP1P31.y, extensionLinePaint);
        canvas.drawLine(pP2P40.x, pP2P40.y, pP2P41.x, pP2P41.y, extensionLinePaint);
    }

    private void updateMidLines() {
        float minVal = DimensHelper.dpToPx(80f, context);
        if (point3.getY() - point1.getY() > minVal) {
            midLine1.setX(point1.getX());
            midLine1.setY((point1.getY() + point3.getY()) / 2f);
            midLine1.setVisibility(VISIBLE);
        } else {
            midLine1.setVisibility(GONE);
        }

        if (point2.getX() - point1.getX() > minVal) {
            midLine2.setY(point1.getY());
            midLine2.setX((point1.getX() + point2.getX()) / 2f);
            midLine2.setVisibility(VISIBLE);
        } else {
            midLine2.setVisibility(GONE);
        }

        if (point4.getY() - point2.getY() > minVal) {
            midLine3.setX(point2.getX());
            midLine3.setY((point2.getY() + point4.getY()) / 2f);
            midLine3.setVisibility(VISIBLE);
        } else {
            midLine3.setVisibility(GONE);
        }

        if (point4.getX() - point3.getX() > minVal) {
            midLine4.setY(point4.getY());
            midLine4.setX((point3.getX() + point4.getX()) / 2f);
            midLine4.setVisibility(VISIBLE);
        } else {
            midLine4.setVisibility(GONE);
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
        return p1.y == p2.y ? p1.y : (-(p1.x - p2.x) * -y - p1.x * p2.y + p2.x * p1.y) / (p1.y - p2.y);
    }

    private float lineFunY(float x, PointF p1, PointF p2) {
        return p1.x == p2.x ? p1.x : ((-(p1.x * p2.y) + (p2.x * p1.y) - (p1.y - p2.y) * x) / (p1.x - p2.x));
    }

    public void setImage(Bitmap bm) {
        resetPoints(getWidth(), getHeight());
        midLine1.setVisibility(VISIBLE);
        midLine2.setVisibility(VISIBLE);
        midLine3.setVisibility(VISIBLE);
        midLine4.setVisibility(VISIBLE);
        editBm = bm;
        mCanvas = new Canvas(editBm);
    }

    public void setViewModel(CropToolViewModel model, Fragment fragment) {
        model.getKeepRatio().observe(fragment.getViewLifecycleOwner(), ratio -> keepRatio = ratio);
    }

    public CropView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CropView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setNewSelection(ProjectViewModel projectViewModel, Fragment activity) {
        projectViewModel.getTransformationState().observe(activity.getViewLifecycleOwner(), v -> {
            if (v == Enums.TransformationState.CANCEL_CROP) cancelTransformation();
            if (v == Enums.TransformationState.CONFIRM_CROP) {
                projectViewModel.addUndoStep(projectViewModel.getProjectModel().getValue().getCurrentLayerModel());
                List<PointF> p = getPointsRelative();

                int originalHeight = editBm.getHeight();
                int originalWidth = editBm.getWidth();
                int x = (int) (p.get(0).x * (originalWidth / 100f));
                int y = (int) (p.get(0).y * (originalHeight / 100f));
                int w = (int) (p.get(3).x * (originalWidth / 100f)) - x;
                int h = (int) (p.get(3).y * (originalHeight / 100f)) - y;

                Bitmap croppedBm = Bitmap.createBitmap(editBm, x, y, w, h);

                float scale = (h / originalHeight > w / originalWidth) ?
                        originalHeight / (float) h : originalWidth / (float) w;

                Matrix transformation = new Matrix();
                transformation.preTranslate((originalWidth - w * scale) / 2f, (originalHeight - h * scale) / 2f);
                transformation.preScale(scale, scale);

                Paint paint = new Paint();
                paint.setFilterBitmap(true);
                mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                mCanvas.drawBitmap(croppedBm, transformation, paint);

                projectViewModel.getProjectModel().getValue().setBitmapToActiveLayer(editBm);
                projectViewModel.invalidate();

                resetPoints(getWidth(), getHeight());
                setImage(projectViewModel.getProjectModel().getValue().getCurrentBitmap());
            }
        });
        setImage(projectViewModel.getProjectModel().getValue().getCurrentBitmap());
        resetPoints(getWidth(), getHeight());
    }

    public void cancelTransformation() {
        resetPoints(getWidth(), getHeight());
    }

    private class PointTouchListener implements OnTouchListener {
        PointF downPt = new PointF();
        PointF startPt = new PointF();
        PointF p1 = new PointF();
        PointF p2 = new PointF();
        PointF p3 = new PointF();
        PointF p4 = new PointF();
        private final int pointView;

        PointTouchListener(int point) {
            this.pointView = point;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE: {
                    PointF mv = new PointF(event.getX() - downPt.x, event.getY() - downPt.y);
                    float posX = (startPt.x + mv.x);
                    float posY = (startPt.y + mv.y);

                    int minSize = 5;
                    if (keepRatio) {
                        switch (pointView) {
                            case 1:
                                posX = Math.min(posX, p2.x - minSize);
                                setPointValues();
                                posX = -lineFunY(0f, p1, p4) > 0 ?
                                        MathUtils.clamp(posX, -v.getWidth() / 2f, getWidth() - (v.getWidth() / 2f)) :
                                        MathUtils.clamp(posX, lineFunX(-v.getWidth() / 2f, p1, p4), lineFunX(getWidth(), p1, p4));
                                posY = -lineFunY(posX, p1, p4);
                                break;
                            case 2:
                                posX = Math.max(posX, p1.x + minSize);
                                setPointValues();
                                posX = -lineFunY(getWidth(), p2, p3) > 0 ?
                                        MathUtils.clamp(posX, -v.getWidth() / 2f, getWidth() - (v.getWidth() / 2f)) :
                                        MathUtils.clamp(posX, posX, lineFunX(-v.getWidth() / 2f, p2, p3));
                                posY = -lineFunY(posX, p2, p3);
                                break;
                            case 3:
                                posX = Math.min(posX, p4.x - minSize);
                                setPointValues();
                                posX = -lineFunY(0, p3, p2) < getHeight() ?
                                        MathUtils.clamp(posX, -v.getWidth() / 2f, getWidth() - (v.getWidth() / 2f)) :
                                        MathUtils.clamp(posX, lineFunX(-v.getWidth() / 2f + getHeight(), p3, p2), posX);
                                posY = -lineFunY(posX, p3, p2);
                                break;
                            case 4:
                                posX = Math.max(posX, p3.x + minSize);
                                setPointValues();
                                posX = -lineFunY(getWidth(), p1, p4) < getHeight() ?
                                        MathUtils.clamp(posX, posX, getWidth() - (v.getWidth() / 2f)) :
                                        MathUtils.clamp(posX, posX, lineFunX(-v.getWidth() / 2f + getHeight(), p1, p4));
                                posY = -lineFunY(posX, p1, p4);
                                break;
                        }
                    } else {
                        switch (pointView) {
                            case 1:
                                posX = Math.min(posX, p2.x - minSize);
                                break;
                            case 2:
                                posX = Math.max(posX, p1.x + minSize);
                                break;
                            case 3:
                                posX = Math.min(posX, p4.x - minSize);
                                break;
                            case 4:
                                posX = Math.max(posX, p3.x + minSize);
                                break;
                        }

                        posX = MathUtils.clamp(posX, -v.getWidth() / 2f, getWidth() - (v.getWidth() / 2f));
                        posY = MathUtils.clamp(posY, -v.getHeight() / 2f, getHeight() - (v.getHeight() / 2f));
                    }

                    if (pointView == 1 || pointView == 3)
                        posX = MathUtils.clamp(posX, posX, point2.getX());

                    if (pointView == 2 || pointView == 4)
                        posX = MathUtils.clamp(posX, point1.getX(), posX);

                    if (pointView == 1 || pointView == 2)
                        posY = MathUtils.clamp(posY, posY, point3.getY());

                    if (pointView == 3 || pointView == 4)
                        posY = MathUtils.clamp(posY, point1.getY(), posY);

                    v.setX(posX);
                    v.setY(posY);

                    switch (pointView) {
                        case 1: {
                            point2.setY(posY);
                            point3.setX(posX);
                            break;
                        }
                        case 2: {
                            point1.setY(posY);
                            point4.setX(posX);
                            break;
                        }
                        case 3: {
                            point1.setX(posX);
                            point4.setY(posY);
                            break;
                        }
                        case 4: {
                            point2.setX(posX);
                            point3.setY(posY);
                            break;
                        }
                    }

                    updateMidLines();
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

        private void setPointValues() {
            p1.set(point1.getX(), point1.getY());
            p2.set(point2.getX(), point2.getY());
            p3.set(point3.getX(), point3.getY());
            p4.set(point4.getX(), point4.getY());
        }
    }

    private class MidLineTouchListener implements OnTouchListener {
        PointF downPt = new PointF();
        PointF startPt = new PointF();
        private final int lineView;

        MidLineTouchListener(int point) {
            this.lineView = point;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {

                case MotionEvent.ACTION_MOVE: {
                    PointF mv = new PointF(event.getX() - downPt.x, event.getY() - downPt.y);
                    float posX = (startPt.x + mv.x);
                    float posY = (startPt.y + mv.y);

                    posX = MathUtils.clamp(posX, -v.getWidth() / 2f, getWidth() - (v.getWidth() / 2f));
                    posY = MathUtils.clamp(posY, -v.getHeight() / 2f, getHeight() - (v.getHeight() / 2f));

                    if (lineView == 1) posX = MathUtils.clamp(posX, -pointSize / 2f, point2.getX());
                    if (lineView == 2) posY = MathUtils.clamp(posY, -pointSize / 2f, point3.getY());
                    if (lineView == 3) posX = MathUtils.clamp(posX, point1.getX(), getWidth());
                    if (lineView == 4) posY = MathUtils.clamp(posY, point1.getY(), getHeight());

                    v.setX(posX);
                    v.setY(posY);

                    switch (lineView) {
                        case 1: {
                            point1.setX(posX);
                            point3.setX(posX);
                            break;
                        }
                        case 2: {
                            point1.setY(posY);
                            point2.setY(posY);
                            break;
                        }
                        case 3: {
                            point2.setX(posX);
                            point4.setX(posX);
                            break;
                        }
                        case 4: {
                            point3.setY(posY);
                            point4.setY(posY);
                            break;
                        }
                    }
                    updateMidLines();
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

                    mv.set(
                            MathUtils.clamp(mv.x, -point1.getX() - pointSize / 2f, getWidth() - point2.getX() - (pointSize / 2f)),
                            MathUtils.clamp(mv.y, -point1.getY() - pointSize / 2f, getHeight() - point3.getY() - (pointSize / 2f))
                    );

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

                    updateMidLines();
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
