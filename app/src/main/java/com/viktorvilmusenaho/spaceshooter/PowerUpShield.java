package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PowerUpShield extends PowerUp {

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
            canvas.drawCircle(_game._player.centerX(), _game._player.centerY(), (_game._player._width / 2), paint);
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
