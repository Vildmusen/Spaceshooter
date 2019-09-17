package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;

public class EnemyMeteor extends BitmapEntity {

    private static int METEOR_HEIGHT = 40;
    private static float METEOR_VELOCITY = 4;

    EnemyMeteor(Context context) {
        super();
        loadResources(context);
        _velX = -(METEOR_VELOCITY / 2);
        _velY = METEOR_VELOCITY;
        int resID = R.drawable.meteor2;
        loadBitmap(resID, METEOR_HEIGHT);
        respawn();
    }

    private void loadResources(Context context){
        try{
            METEOR_HEIGHT = context.getResources().getInteger(R.integer.meteor_height);
            METEOR_VELOCITY = (float) context.getResources().getInteger(R.integer.meteor_velocity);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    void respawn() {
        _x = _game._rng.nextInt(_game.STAGE_WIDTH / 2) + _game.STAGE_WIDTH;
        _y = -(_game._rng.nextInt(_game.STAGE_HEIGHT));
    }

    @Override
    void update() {
        _y += _velY;
        _x += _velX - _game._playerSpeed;
        if (right() < 0 || top() > _game.STAGE_HEIGHT) {
            respawn();
        }
    }

    @Override
    void onCollision(Entity that) {
        respawn();
    }
}
