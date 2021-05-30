package com.example.photoedge.ui.customview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.math.MathUtils;

import com.example.photoedge.helpers.DimensHelper;

import org.jetbrains.annotations.NotNull;

public class TemplateRatioView extends AppCompatImageView {

    private int lastWidth = 0;
    private int lastHeight = 0;
    private int maxWidth = 0;
    private int maxHeight = 0;
    private final int margin = (int) DimensHelper.dpToPx(96, getContext());

    public TemplateRatioView(Context context) {
        super(context);
    }

    public TemplateRatioView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TemplateRatioView(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getSize(widthMeasureSpec) > maxWidth) {
            maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        }

        if (MeasureSpec.getSize(heightMeasureSpec) > maxHeight) {
            maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setPreviewSize(int width, int height) {
        if (width == 0 || height == 0) return;
        float wRatio = ((float) (maxWidth - margin) / width);
        float hRatio = ((float) (maxHeight - margin) / height);
        int w, h;

        if (width > height) {
            w = (int) (width * wRatio);
            h = (int) (height * wRatio);
        } else {
            w = (int) (width * hRatio);
            h = (int) (height * hRatio);
        }

        int mWidth = MathUtils.clamp(w, 0, maxWidth - margin);
        int mHeight = MathUtils.clamp(h, 0, maxHeight - margin);

        if (lastWidth <= 0) {
            lastWidth = maxWidth - margin;
            lastHeight = maxHeight - margin;
        }

        ValueAnimator animW = ValueAnimator.ofInt(lastWidth, mWidth);
        ValueAnimator animH = ValueAnimator.ofInt(lastHeight, mHeight);
        animW.setDuration(500);
        animH.setDuration(500);

        lastWidth = mWidth;
        lastHeight = mHeight;


        animW.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = val;
            setLayoutParams(layoutParams);
        });

        animH.addUpdateListener(animation -> {
            int val = (Integer) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = val;
            setLayoutParams(layoutParams);
        });
        animW.start();
        animH.start();
    }

}
