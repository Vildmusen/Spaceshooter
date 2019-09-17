package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

abstract class BitmapEntity extends Entity {

    private Bitmap _bitmap = null;

    BitmapEntity() {
    }

    void loadBitmap(int resID, int height) {
        destroy();
        Bitmap temp = BitmapFactory.decodeResource(
                _game.getContext().getResources(),
                resID
        );

        try {
            _bitmap = Utils.scaleToTargetHeight(temp, height);
            temp.recycle();
            _width = _bitmap.getWidth();
            _height = _bitmap.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        canvas.drawBitmap(_bitmap, _x, _y, paint);
    }

    @Override
    void destroy() {
        if (_bitmap != null) {
            _bitmap.recycle();
            _bitmap = null;
        }
    }
}
