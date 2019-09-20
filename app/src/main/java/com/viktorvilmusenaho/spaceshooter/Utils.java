package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Bitmap;

abstract class Utils {

    static Bitmap scaleToTargetHeight(Bitmap src, final int targetHeight) {
        float ratio = targetHeight / (float) src.getHeight();
        int newH = (int) (src.getHeight() * ratio);
        int newW = (int) (src.getWidth() * ratio);
        return Bitmap.createScaledBitmap(src, newW, newH, true);
    }

    static float wrap(float val, final float min, final float max) {
        if (val < min) {
            val = max;
        } else if (val > max) {
            val = min;
        }
        return val;
    }

    static float clamp(float val, final float min, final float max) {
        if (val > max) {
            val = max;
        } else if (val < min) {
            val = min;
        }
        return val;
    }

}
