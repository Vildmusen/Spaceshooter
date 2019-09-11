package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Star extends Entity {

    private final static int _color = 0xFFFF66FF; //TODO magic value
    private float _radius;

    Star(){
        _x = _game._rng.nextInt(Game.STAGE_WIDTH);
        _y = _game._rng.nextInt(Game.STAGE_HEIGHT);
        _radius = _game._rng.nextInt(6) + 2; //TODO magic values
        _width = _radius * 2;
        _height = _radius * 2;
        _velX = -4f; //TODO magic values
    }

    @Override
    void update() {
        _velX = -(_game._playerSpeed);
        _x += _velX;
        _x = Utils.wrap(_x, 0-_width, Game.STAGE_WIDTH);
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        paint.setColor(_color);
        canvas.drawCircle(_x+_radius, _y+_radius, _radius, paint);
    }
}
