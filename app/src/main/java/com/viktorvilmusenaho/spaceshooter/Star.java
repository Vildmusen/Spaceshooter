package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Star extends BitmapEntity {

    private final static int _color = 0xFFFF66FF; //TODO magic value
    private final static int STAR_MAX_SIZE = 5;
    private final static int STAR_MIN_SIZE = 1;
    private final static float STAR_VEL = 4f;
    private float _radius;

    Star() {
        _x = _game._rng.nextInt(Game.STAGE_WIDTH);
        _y = _game._rng.nextInt(Game.STAGE_HEIGHT);
        _radius = _game._rng.nextInt(STAR_MAX_SIZE) + STAR_MIN_SIZE;
        _width = _radius * 2;
        _height = _radius * 2;
        modifySpeed(STAR_VEL);
    }

    @Override
    void update() {
        modifySpeed(_game._playerSpeed);
        _x += _velX;
        _x = Utils.wrap(_x, 0 - _width, Game.STAGE_WIDTH);
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        paint.setColor(_color);
        canvas.drawCircle(_x + _radius, _y + _radius, _radius, paint);
    }

    void modifySpeed(float speed){
        _velX= -(speed * 1 + (_radius/5));
    }

}
