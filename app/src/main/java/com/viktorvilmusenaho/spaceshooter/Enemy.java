package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;

import static java.lang.Math.sin;

class Enemy extends BitmapEntity {

    private static int ENEMY_HEIGHT = 80;
    private static int ENEMY_SPAWN_OFFSET = _game.STAGE_WIDTH;
    private static int ENEMY_SPRITE_COUNT = 3;

    private float _patternCounter = 0f;

    Enemy(Context context) {
        super();
        loadResources(context);
        int resID = randomizeSprite();
        loadBitmap(resID, ENEMY_HEIGHT);
        _patternCounter += 3.14 * _game._rng.nextFloat();
        respawn();
    }

    private void loadResources(Context context){
        try {
            ENEMY_HEIGHT = context.getResources().getInteger(R.integer.enemy_height);
            ENEMY_SPRITE_COUNT = context.getResources().getInteger(R.integer.enemy_sprite_count);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private int randomizeSprite(){
        switch (_game._rng.nextInt(ENEMY_SPRITE_COUNT)) {
            case 0:
                return R.drawable.spaceship1_2; //TODO sprite names as resource
            case 1:
                return R.drawable.spaceship2_2;
            case 2:
                return R.drawable.spaceship3_2;
        }
        return R.drawable.spaceship1_2;
    }

    @Override
    void respawn() {
        _x = _game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        _y = _game._rng.nextInt(_game.STAGE_HEIGHT - ENEMY_HEIGHT);
    }

    @Override
    void update() {
        _velX = -(_game._playerSpeed);
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
