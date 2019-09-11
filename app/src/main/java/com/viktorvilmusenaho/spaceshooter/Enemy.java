package com.viktorvilmusenaho.spaceshooter;

public class Enemy extends BitmapEntity {

    private final static int ENEMY_HEIGHT = 80;
    private final static int ENEMY_SPAWN_OFFSET = Game.STAGE_WIDTH;

    Enemy() {
        super();
        _x = Game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        _y = _game._rng.nextInt(Game.STAGE_HEIGHT - ENEMY_HEIGHT);
        int resID = R.drawable.spaceship1_2;
        switch (_game._rng.nextInt(3)) {
            case 0:
                resID = R.drawable.spaceship1_2;
                break;
            case 1:
                resID = R.drawable.spaceship2_2;
                break;
            case 2:
                resID = R.drawable.spaceship3_2;
                break;
        }
        loadBitmap(resID, ENEMY_HEIGHT);
        respawn();
    }

    @Override
    void respawn() {
        _x = Game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        _y = _game._rng.nextInt(Game.STAGE_HEIGHT - ENEMY_HEIGHT);
    }

    @Override
    void update() {
        _velX = -(_game._playerSpeed);
        _x += _velX;
        if (right() < 0) {
            _x = Game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
        }
    }

    @Override
    void onCollision(Entity that) {
        _x = Game.STAGE_WIDTH + _game._rng.nextInt(ENEMY_SPAWN_OFFSET);
    }
}
