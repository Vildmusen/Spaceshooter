package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import static java.lang.Math.sin;

class Enemy extends BitmapEntity {

    private static final String TAG = "Enemy";
    private static int ENEMY_HEIGHT = 80;
    private static int ENEMY_SPAWN_OFFSET = _game.STAGE_WIDTH;
    private static int ENEMY_SPRITE_COUNT = 3;
    private static float DIFFICULTY_MULTIPLIER = 1;

    private float _patternCounter = 0f;

    Enemy(Context context) {
        super();
        loadResources(context);
        int resID = randomizeSprite();
        loadBitmap(resID, ENEMY_HEIGHT);
        _patternCounter += 3.14 * _game._rng.nextFloat();
        respawn();
    }

    private void loadResources(Context context) {
        try {
            ENEMY_HEIGHT = context.getResources().getInteger(R.integer.enemy_height);
            ENEMY_SPRITE_COUNT = context.getResources().getInteger(R.integer.enemy_sprite_count);
            try{
                DIFFICULTY_MULTIPLIER = Float.parseFloat(context.getResources().getString(R.string.difficulty_multiplier));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private int randomizeSprite() {
        switch (_game._rng.nextInt(ENEMY_SPRITE_COUNT)) {
            case 0:
                return R.drawable.spaceship1_2; //TODO sprite names as resource?
            case 1:
                return R.drawable.spaceship2_2;
            case 2:
                return R.drawable.spaceship3_2;
        }
        return R.drawable.spaceship1_2;
    }

    @Override
    void render(Canvas canvas, Paint paint) {
        super.render(canvas, paint);
        Log.d(TAG, "im rendering at " + _x);
    }

    @Override
    void respawn() {
        _x = _game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        _y = _game._rng.nextInt(_game.STAGE_HEIGHT - ENEMY_HEIGHT);
    }

    @Override
    void update() {
        _velX = -(_game._playerSpeed * (DIFFICULTY_MULTIPLIER + (_patternCounter/1000)));
        _x += _velX;

        _patternCounter += 0.01f;
        _y += (float) sin(_patternCounter);

        if (right() < 0) {
            _x = _game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        }
    }

    @Override
    void onCollision(Entity that) {
        respawn();
    }
}
