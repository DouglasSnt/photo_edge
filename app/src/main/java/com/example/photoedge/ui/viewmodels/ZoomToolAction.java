
package com.example.photoedge.ui.viewmodels;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.core.math.MathUtils;
import androidx.lifecycle.ViewModel;

import com.example.photoedge.models.ProjectModel;

public class ZoomToolAction extends ViewModel {

    private final PointF lastPoint = new PointF();
    private boolean isDragging = false;

    @SuppressLint("ClickableViewAccessibility")
    public View.OnTouchListener getToolAction(ProjectViewModel projectViewModel, Activity activity) {

        ScaleGestureDetector.SimpleOnScaleGestureListener gestureDetector = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

            final float maxZoom = 16f;
            float centerX;
            float centerY;
            float initialZoom;

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                ProjectModel projectModel = projectViewModel.getProjectModel().getValue();
                initialZoom = projectModel.getCurrentZoom();

                int mWidth = projectModel.getWidth();
                int mHeight = projectModel.getHeight();
                PointF tr = projectModel.getPreTranslate();

                float nWidth = mWidth / initialZoom;
                float nHeight = mHeight / initialZoom;

                centerX = tr.x + (nWidth / 2);
                centerY = tr.y + (nHeight / 2);

                return super.onScaleBegin(detector);
            }

            @Override
            public boolean onScale(ScaleGestureDetector event) {
                ProjectModel projectModel = projectViewModel.getProjectModel().getValue();
                float delta = (event.getCurrentSpan() / event.getPreviousSpan());

                float currentZoom = projectModel.getCurrentZoom();
                currentZoom += ((delta - 1f) * 4);
                currentZoom = MathUtils.clamp(currentZoom, 1f, maxZoom);

                projectModel.setCurrentZoom(currentZoom);

                int mWidth = projectModel.getWidth();
                int mHeight = projectModel.getHeight();

                float nWidth = mWidth / currentZoom;
                float nHeight = mHeight / currentZoom;

                if (centerX + nWidth / 2 > mWidth) {
                    centerX = centerX - (centerX + (nWidth / 2) - mWidth);
                }
                if (centerY + nHeight / 2 > mHeight) {
                    centerY = centerY - (centerY + (nHeight / 2) - mHeight);
                }

                PointF newTr = new PointF(
                        Math.max(0, centerX - (nWidth / 2)),
                        Math.max(0, centerY - (nHeight / 2))
                );
                projectModel.setPreTranslate(newTr);
                projectViewModel.invalidate();
                return true;
            }
        };

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(activity, gestureDetector);

        return (v, e) -> {
            scaleGestureDetector.onTouchEvent(e);

            ProjectModel projectModel = projectViewModel.getProjectModel().getValue();

            int fWidth = projectModel.getWidth();
            int fHeight = projectModel.getHeight();
            Float currentZoom = projectModel.getCurrentZoom();

            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    lastPoint.set(e.getX(), e.getY());
                    isDragging = true;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!isDragging) break;
                    float lx = (fWidth * (lastPoint.x / v.getWidth()));
                    float ly = (fHeight * (lastPoint.y / v.getHeight()));
                    float x = (fWidth * (e.getX() / v.getWidth()));
                    float y = (fHeight * (e.getY() / v.getHeight()));
                    PointF tr = projectModel.getPreTranslate();

                    float deltaX = (lx - x) / currentZoom;
                    float deltaY = (ly - y) / currentZoom;

                    PointF newTr = new PointF(
                            MathUtils.clamp(tr.x + deltaX, 0, fWidth - (fWidth / currentZoom)),
                            MathUtils.clamp(tr.y + deltaY, 0, fHeight - (fHeight / currentZoom))
                    );
                    projectModel.setPreTranslate(newTr);

                    lastPoint.set(e.getX(), e.getY());
                    projectViewModel.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isDragging = false;
                    break;
                }
                default:
            }

            if (e.getPointerCount() > 1) {
                isDragging = false;
            }

            return true;
        };
    }
}
