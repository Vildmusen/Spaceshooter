package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PowerUpShield extends PowerUp {

    private static int SHIELD_THICKNESS = 5;

    public boolean _isActive = false;

    PowerUpShield(Context context) {
        super(context, R.drawable.power_up_shield);
    }

    @Override
    void respawn() {
        _isActive = false;
        super.respawn();
    }

    @Override
    void update() {
        if(!_isActive){
            super.update();
        }
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        if(!_isActive){
            super.render(canvas, paint);
        } else {
            paint.setColor(Color.BLUE);
            canvas.drawCircle(_game._player.centerX(), _game._player.centerY(), (_game._player._width / 2), paint);
            paint.setColor(Color.BLACK);
            canvas.drawCircle(_game._player.centerX(), _game._player.centerY(), (_game._player._width / 2) - SHIELD_THICKNESS, paint);
        }
    }

    @Override
    void onCollision(Entity that) {
        if(!_isActive){
            super.onCollision(that);
            _game._player.setShield(this);
            _isActive = true;
        }
    }
}
