package com.viktorvilmusenaho.spaceshooter;

public class EnemyMeteor extends BitmapEntity {

    private final static int ENEMY_HEIGHT = 40;
    private final static float METEOR_VELOCITY = 4;

    EnemyMeteor() {
        super();
        _velX = -(METEOR_VELOCITY / 2);
        _velY = METEOR_VELOCITY;
        int resID = R.drawable.meteor2;
        loadBitmap(resID, ENEMY_HEIGHT);
        respawn();
    }

    @Override
    void respawn() {
        _x = _game._rng.nextInt(Game.STAGE_WIDTH / 2) + _game.STAGE_WIDTH * 2;
        _y = -(_game._rng.nextInt(Game.STAGE_HEIGHT));
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
