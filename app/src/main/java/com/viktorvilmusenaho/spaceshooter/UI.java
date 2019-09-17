package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

public class UI {

    public static final String TAG = "UI";
    public static final String PREFS = "com.viktorvilmusenaho.spaceshooter";
    public static final String LONGEST_DIST = "longest_distance";
    public static String GAME_OVER = "GAME OVER!";
    public static String RESTART_MESSAGE = "(Tap screen to restart)";
    public static String HEALTH = "Health: ";
    public static String DISTANCE = "Distance: ";

    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;
    private int _maxDistanceTraveled = 0;

    public UI(Context context){
        loadResources(context);
        _prefs = context.getSharedPreferences(PREFS, context.MODE_PRIVATE);
        _editor = _prefs.edit();
        _maxDistanceTraveled = _prefs.getInt(LONGEST_DIST, 0);
    }

    private void loadResources(Context context){
        try{
            GAME_OVER = context.getResources().getString(R.string.game_over_message);
            RESTART_MESSAGE = context.getResources().getString(R.string.restart_message);
            HEALTH = context.getResources().getString(R.string.health_text);
            DISTANCE = context.getResources().getString(R.string.distance_text);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveHighScore(int distanceTraveled){
        if (distanceTraveled > _maxDistanceTraveled) {
            _maxDistanceTraveled = distanceTraveled;
            _editor.putInt(LONGEST_DIST, _maxDistanceTraveled);
            _editor.apply();
        }
    }
}
