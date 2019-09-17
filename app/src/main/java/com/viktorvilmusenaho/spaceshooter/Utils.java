package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Bitmap;
import android.graphics.Matrix;

abstract class Utils {

//    static Matrix _matrix = new Matrix();
//
//    public static Bitmap flipBitmap(final Bitmap src, final boolean horizontal){
//        _matrix.reset();
//        final int cx = src.getWidth()/2;
//        final int cy = src.getHeight()/2;
//        if(horizontal){
//            _matrix.postScale(1, -1, cx, cy);
//        } else {
//            _matrix.postScale(-1,1, cx, cy);
//        }
//        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), _matrix, true);
//    }

    static Bitmap scaleToTargetHeight(Bitmap src, final int targetHeight){
        float ratio = targetHeight / (float) src.getHeight();
        int newH = (int) (src.getHeight() * ratio);
        int newW = (int) (src.getWidth() * ratio);
        return Bitmap.createScaledBitmap(src, newW, newH, true);
    }

    static float wrap(float val, final float min, final float max){
        if(val < min) {
            val = max;
        } else if(val > max){
            val = min;
        }
        return val;
    }

    static float clamp(float val, final float min, final float max){
        if(val > max){
            val = max;
        } else if (val < min){
            val = min;
        }
        return val;
    }

}
