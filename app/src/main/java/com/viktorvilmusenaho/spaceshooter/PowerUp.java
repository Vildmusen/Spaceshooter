package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

abstract class PowerUp extends BitmapEntity {

    private static final String TAG = "PowerUp";
    private static int POWER_UP_SIZE = 15;
    private static int POWER_UP_SCARCITY = 4;
    private static float POWER_UP_VEL = 4f;
    private int POWER_UP_SPAWN_OFFSET = (_game.STAGE_WIDTH * (1 + _game._rng.nextInt(POWER_UP_SCARCITY)));

    PowerUp(Context context, int resID) {
        loadResources(context);
        loadBitmap(resID, POWER_UP_SIZE);
        _velX = -POWER_UP_VEL;
    }

    private void loadResources(Context context) {
        POWER_UP_SIZE = context.getResources().getInteger(R.integer.power_up_size);
        POWER_UP_VEL = context.getResources().getInteger(R.integer.power_up_velocity);
    }

    @Override
    void respawn() {
        _x = _game.STAGE_WIDTH + _game._rng.nextInt(POWER_UP_SPAWN_OFFSET);
        _y = _game._rng.nextInt(_game.STAGE_HEIGHT - (int) _height);
    }

    @Override
    void update() {
        _x += -_game._playerSpeed;
        _x = Utils.wrap(_x, 0 - _width, _game.STAGE_WIDTH * 4);
    }

}
