package com.viktorvilmusenaho.spaceshooter;

import android.graphics.Bitmap;

public class Player extends BitmapEntity {

    private Bitmap _bitmap = null;
    int _health = 3; //TODO magic values
    private final static int PLAYER_HEIGHT = 100; //TODO magic values
    private final static int STARTING_POSITION = 40;
    private final static int STARTING_HEALTH = 3;
    private final static float ACC = 1.1f;
    private final static float MIN_VEL = 1f;
    private final static float MAX_VEL = 30f;
    private final static float GRAVITY = 1.1f;
    private final static float LIFT = -(GRAVITY * 2);
    private final static float DRAG = 0.97f;

    Player(){
        super();
        loadBitmap(R.drawable.angler_ship, PLAYER_HEIGHT);
        respawn();
    }

    @Override
    void respawn() {
        _x = STARTING_POSITION;
        _health = STARTING_HEALTH;
        _velX = 0f;
        _velY = 0f;
    }

    @Override
    void update() {
        _velX *= DRAG;
        _velY += GRAVITY;
        if(_game._isBoosting){
            _velX *= ACC;
            _velY += LIFT;
        }
        _velX = Utils.clamp(_velX, MIN_VEL, MAX_VEL);
        _velY = Utils.clamp(_velY, -MAX_VEL, MAX_VEL/2);
        _y += _velY;
        _x = Utils.wrap(_x, -_width, Game.STAGE_WIDTH);
        _y = Utils.clamp(_y, 0, Game.STAGE_HEIGHT-_height);
        _game._playerSpeed = _velX;
    }

    @Override
    void onCollision(Entity that) {
        //TODO recovery frames
        _health--;
    }
}
