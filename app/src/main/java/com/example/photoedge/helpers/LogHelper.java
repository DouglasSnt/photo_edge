package com.example.photoedge.helpers;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

@SuppressLint("DefaultLocale")
public class LogHelper {

    public static String mLog(Rect r) {
        return String.format("[%d, %d, %d, %d] ", r.left, r.top, r.right, r.bottom);
    }

    public static String mLog(RectF r) {
        return String.format("[%.1f, %.1f, %.1f, %.1f] ", r.left, r.top, r.right, r.bottom);
    }

    public static String mLog(Point p) {
        return String.format("[%d, %d] ", p.x, p.y);
    }

    public static String mLog(PointF p) {
        return String.format("[%.1f, %.1f] ", p.x, p.y);
    }

}
