package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

class PowerUp extends BitmapEntity {

    private static int POWER_UP_SIZE = 15;
    private static int POWER_UP_SCARCITY = 4;
    private static final int POWER_UP_SPAWN_OFFSET = (_game.STAGE_WIDTH * _game._rng.nextInt(POWER_UP_SCARCITY));
    private static float POWER_UP_VEL = 4f;

    PowerUp(Context context){
        loadResources(context);
        int resID = R.drawable.power_up_kill;
        loadBitmap(resID, POWER_UP_SIZE);
        _velX = -POWER_UP_VEL;
    }

    @Override
    void respawn() {
        _x = _game.STAGE_WIDTH + _game._rng.nextInt(POWER_UP_SPAWN_OFFSET);
        _y = _game._rng.nextInt(_game.STAGE_HEIGHT - (int)_height);
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        super.render(canvas, paint);
    }

    @Override
    void update() {
        _x -= _velX;
        _x = Utils.wrap(_x, -(_game.STAGE_WIDTH * 4), _game.STAGE_WIDTH);
    }

    @Override
    void destroy() {
        super.destroy();
    }

    private void loadResources(Context context){
        POWER_UP_SIZE = context.getResources().getInteger(R.integer.power_up_size);
        POWER_UP_VEL = context.getResources().getInteger(R.integer.power_up_velocity);
    }

}
