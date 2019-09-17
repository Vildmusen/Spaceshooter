package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

class PlayerProjectile extends Entity {

    private static int COLOR = 0xFFFF0000;
    private static int WIDTH = 10;
    private static int HEIGHT = 2;
    private static float PROJECTILE_VEL = 6f;

    private Rect _shot = null;
    private float _playerWidth;
    private float _playerHeight;

    PlayerProjectile(Context context, float playerX, float playerY, float playerWidth, float playerHeight) {
        loadResources(context);
        _playerWidth = playerWidth;
        _playerHeight = playerHeight;
        spawn(playerX, playerY);
        _velX = PROJECTILE_VEL;
    }

    private void spawn(float x, float y) {
        _x = x;
        _y = y;
        _shot = new Rect(
                (int) (_x + _playerWidth),
                (int) (_y + (_playerHeight / 2)),
                (int) (_x + WIDTH + _playerWidth),
                (int) (_y + HEIGHT + (_playerHeight /2)));
    }

    void despawn(){
        _shot = null;
    }

    boolean isOnScreen(){
        return _x < _game.STAGE_WIDTH - WIDTH;
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

    @Override
    void update() {
        if (_x > _game.STAGE_WIDTH - WIDTH) {
            despawn();
        } else {
            _x += _velX;
            _shot = new Rect(
                    (int) (_x + _playerWidth),
                    (int) (_y + (_playerHeight / 2)),
                    (int) (_x + WIDTH + _playerWidth),
                    (int) (_y + HEIGHT + (_playerHeight /2)));
        }
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        if(_shot != null){
            paint.setColor(COLOR);
            canvas.drawRect(_shot, paint);
        }
    }

    @Override
    void onCollision(Entity that) {
        despawn();
    }
}
