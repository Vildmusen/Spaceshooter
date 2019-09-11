package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class BitmapEntity extends Entity {
    protected Bitmap _bitmap = null;
    BitmapEntity(){}

    protected void loadBitmap(int resID, int height){
        destroy();
        Bitmap temp = BitmapFactory.decodeResource(
                _game.getContext().getResources(),
                resID
        );

        //TODO assert bitmap exists
        _bitmap = Utils.scaleToTargetHeight(temp, height);
        temp.recycle();
        _width = _bitmap.getWidth();
        _height = _bitmap.getHeight();
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        canvas.drawBitmap(_bitmap, _x, _y, paint);
    }

    @Override
    void destroy() {
        if(_bitmap != null){
            _bitmap.recycle();
            _bitmap = null;
        }
    }
}
