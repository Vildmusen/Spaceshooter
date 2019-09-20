package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;

public class PowerUpDestroy extends PowerUp {

    PowerUpDestroy(Context context) {
        super(context, R.drawable.power_up_kill);
    }

    @Override
    void onCollision(Entity that) {
        respawn();
        _game.killAllEnemies();
    }
}
