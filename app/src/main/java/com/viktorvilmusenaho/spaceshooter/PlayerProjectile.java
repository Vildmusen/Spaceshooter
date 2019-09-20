package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;

class PlayerProjectile extends BitmapEntity {

    private static final String TAG = "SHOT";
    private static int HEIGHT = 6;
    private static float PROJECTILE_VEL = 6f;

    public boolean _isActive = false;
    private float _playerWidth;
    private float _playerHeight;

    PlayerProjectile(Context context, float playerWidth, float playerHeight) {
        loadResources(context);
        _playerWidth = playerWidth;
        _playerHeight = playerHeight;
        int resID = R.drawable.rocketszt4;
        loadBitmap(resID, HEIGHT);
        _velX = PROJECTILE_VEL;
    }

    private void loadResources(Context context) {
        try {
            HEIGHT = context.getResources().getInteger(R.integer.projectile_height);
            PROJECTILE_VEL = context.getResources().getInteger(R.integer.projectile_velocity);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void spawn(float x, float y) {
        _x = x + _playerWidth;
        _y = y + (_playerHeight / 2);
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
            return;
        }
        if (left() > _game.STAGE_WIDTH) {
            respawn();
        } else {
            _x += _velX;
        }
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        if (_isActive) {
            super.render(canvas, paint);
        }
    }

    @Override
    void onCollision(Entity that) {
        respawn();
    }

    @Override
    boolean isColliding(Entity that) {
        if (this == that) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return aboutToCollide(that);
    }

    private boolean aboutToCollide(Entity e) {
        return !(right() + _velX <= e.left() + e._velX
                || e.right() + e._velX <= left() + _velX
                || bottom() + _velY <= e.top() + e._velY
                || e.bottom() + e._velY <= top() + _velY);
    }
}
