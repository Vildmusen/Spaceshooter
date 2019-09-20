package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.Resources;

class Player extends BitmapEntity {

    private static int PLAYER_HEIGHT = 100;
    private static int PLAYER_HEALTH = 3;
    private static int STARTING_POSITION = 40;
    private static int PLAYER_LEFT_MARGIN = 120;
    private static float ACC = 1.1f;
    private static float MIN_VEL = 1f;
    private static float MAX_VEL = 15f;
    private static float GRAVITY = 1.1f;
    private static float LIFT = -(GRAVITY * 2);
    private static float DRAG = 0.97f;
    private static int RECOVERY_FRAMES = 64;

    int _health = PLAYER_HEALTH;
    int _graceCounter = 0;
    PowerUpShield _shield = null;

    Player(Context context) {
        super();
        loadResources(context);
        loadBitmap(R.drawable.angler_ship, PLAYER_HEIGHT);
        respawn();
    }

    private void loadResources(Context context) {
        try {
            PLAYER_HEIGHT = context.getResources().getInteger(R.integer.player_height);
            PLAYER_HEALTH = context.getResources().getInteger(R.integer.player_health);
            STARTING_POSITION = context.getResources().getInteger(R.integer.player_starting_position);
            PLAYER_LEFT_MARGIN = context.getResources().getInteger(R.integer.player_left_margin);
            RECOVERY_FRAMES = context.getResources().getInteger(R.integer.recovery_frames);
            try {
                ACC = Float.parseFloat(context.getResources().getString(R.string.player_starting_acceleration));
                MIN_VEL = Float.parseFloat(context.getResources().getString(R.string.player_min_velocity));
                MAX_VEL = Float.parseFloat(context.getResources().getString(R.string.player_max_velocity));
                GRAVITY = Float.parseFloat(context.getResources().getString(R.string.gravity));
                DRAG = Float.parseFloat(context.getResources().getString(R.string.drag));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    void respawn() {
        _x = STARTING_POSITION;
        _health = PLAYER_HEALTH;
        _velX = 0f;
        _velY = 0f;
    }

    @Override
    void update() {
        _velX *= DRAG;
        _velY += GRAVITY;
        if (_game._isBoosting) {
            _velX *= ACC;
            _velY += LIFT;
        }
        _velX = Utils.clamp(_velX, MIN_VEL, MAX_VEL);
        _velY = Utils.clamp(_velY, -MAX_VEL, MAX_VEL / 2);
        _x = PLAYER_LEFT_MARGIN;
        _y += _velY;
        _y = Utils.clamp(_y, 0, _game.STAGE_HEIGHT - _height);
        _game._playerSpeed = _velX;
        if (_graceCounter > 0) {
            _graceCounter--;
        }
    }

    @Override
    void onCollision(Entity that) {
        if(!(that instanceof PowerUp)){
            if (_shield != null && _shield._isActive) {
                _graceCounter = RECOVERY_FRAMES;
                _shield.respawn();
                _shield = null;
                return;
            }
            _graceCounter = RECOVERY_FRAMES;
            _health--;
        }
    }

    public void setShield(PowerUpShield shield) {
        _shield = shield;
        _game._jukebox.play(JukeBox.GAME_START);
    }
}
