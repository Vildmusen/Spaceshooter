package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;

public class UI {

//    RESOURCES
    public static final String TAG = "UI";
    public static final String PREFS = "com.viktorvilmusenaho.spaceshooter";
    public static final String LONGEST_DIST = "longest_distance";
    public static final String GAME_OVER = "GAME OVER!";
    public static final String RESTART_MESSAGE = "(Tap screen to restart)";
    public static final String HEALTH = "Health: ";
    public static final String DISTANCE = "Distance: ";

    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;
    private int _maxDistanceTraveled = 0;

    public UI(Context context){
        _prefs = context.getSharedPreferences(PREFS, context.MODE_PRIVATE);
        _editor = _prefs.edit();
        _maxDistanceTraveled = _prefs.getInt(LONGEST_DIST, 0);
    }

    public void saveHighScore(int distanceTraveled){
        if (distanceTraveled > _maxDistanceTraveled) {
            _maxDistanceTraveled = distanceTraveled;
            _editor.putInt(LONGEST_DIST, _maxDistanceTraveled);
            _editor.apply();
        }
    }
}
