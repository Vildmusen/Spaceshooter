package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

class PlayerProjectile extends Entity {

    private static final String TAG = "SHOT";
    private static int COLOR = 0xFFFF0000;
    private static int WIDTH = 10;
    private static int HEIGHT = 2;
    private static float PROJECTILE_VEL = 6f;

    public boolean _isActive = false;
    private Rect _shot = null;
    private float _playerWidth;
    private float _playerHeight;

    PlayerProjectile(Context context, float playerWidth, float playerHeight) {
        loadResources(context);
        _playerWidth = playerWidth;
        _playerHeight = playerHeight;
        _velX = PROJECTILE_VEL;
    }

    private void loadResources(Context context) {
        try {
            COLOR = context.getResources().getInteger(R.integer.projectile_color);
            WIDTH = context.getResources().getInteger(R.integer.projectile_width);
            HEIGHT = context.getResources().getInteger(R.integer.projectile_height);
            PROJECTILE_VEL = context.getResources().getInteger(R.integer.projectile_velocity);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void spawn(float x, float y) {
        _x = x;
        _y = y;
        _shot = new Rect(
                (int) (_x + _playerWidth),
                (int) (_y + (_playerHeight / 2)),
                (int) (_x + WIDTH + _playerWidth),
                (int) (_y + HEIGHT + (_playerHeight / 2)));
    }

    @Override
    void respawn() {
        _x = 0;
        _y = 0;
        _isActive = false;
    }

    @Override
    void update() {
        if (!_isActive) {
            _shot = null;
            return;
        }
        if (left() > _game.STAGE_WIDTH) {
            respawn();
        } else {
            _x += _velX;
            _shot = new Rect(
                    (int) (_x + _playerWidth),
                    (int) (_y + (_playerHeight / 2)),
                    (int) (_x + WIDTH + _playerWidth),
                    (int) (_y + HEIGHT + (_playerHeight / 2)));
        }
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        if (_shot != null) {
            paint.setColor(COLOR);
            canvas.drawRect(_shot, paint);
        }
    }

    @Override
    void onCollision(Entity that) {
        respawn();
    }
}
