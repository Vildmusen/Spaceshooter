package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

class Star extends BitmapEntity {

    private static int COLOR1 = 0xFFFF66FF;
    private static int COLOR2 = 0xFFFFFFFF;
    private static int COLOR3 = 0xFFFFFF66;
    private static int STAR_MAX_SIZE = 5;
    private static int STAR_MIN_SIZE = 1;
    private static float STAR_VEL = 4f;

    private float _radius;
    private int _randomColor;

    Star(Context context) {
        loadResources(context);
        randomizeColor();
        _x = _game._rng.nextInt(_game.STAGE_WIDTH);
        _y = _game._rng.nextInt(_game.STAGE_HEIGHT);
        _radius = _game._rng.nextInt(STAR_MAX_SIZE) + STAR_MIN_SIZE;
        _width = _radius * 2;
        _height = _radius * 2;
        modifySpeed(STAR_VEL);
    }

    private void loadResources(Context context){
        try{
            COLOR1 = context.getResources().getInteger(R.integer.star_color1);
            COLOR2 = context.getResources().getInteger(R.integer.star_color2);
            COLOR3 = context.getResources().getInteger(R.integer.star_color3);
            STAR_MAX_SIZE = context.getResources().getInteger(R.integer.star_max_size);
            STAR_MIN_SIZE = context.getResources().getInteger(R.integer.star_min_size);
            STAR_VEL = context.getResources().getInteger(R.integer.star_velocity);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private void randomizeColor(){
        _randomColor = COLOR1;
        switch(_game._rng.nextInt(3)){
            case 0:
                _randomColor = COLOR1;
                break;
            case 1:
                _randomColor = COLOR2;
                break;
            case 2:
                _randomColor = COLOR3;
                break;
        }
    }

    @Override
    void update() {
        modifySpeed(_game._playerSpeed);
        _x += _velX;
        _x = Utils.wrap(_x, 0 - _width, _game.STAGE_WIDTH);
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        paint.setColor(_randomColor);
        canvas.drawCircle(_x + _radius, _y + _radius, _radius, paint);
    }

    private void modifySpeed(float speed){
        _velX= -(speed * 1 + (_radius/5));
    }

}
