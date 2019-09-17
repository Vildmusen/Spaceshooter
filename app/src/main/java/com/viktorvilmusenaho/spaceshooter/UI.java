package com.viktorvilmusenaho.spaceshooter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;

class UI {

//    public static final String TAG = "UI";
    static final String PREFS = "com.viktorvilmusenaho.spaceshooter";
    static final String LONGEST_DIST = "longest_distance";
    static String GAME_OVER = "GAME OVER!";
    static String RESTART_MESSAGE = "(Tap screen to restart)";
    static String HEALTH = "Health: ";
    static String DISTANCE = "Distance: ";
    private static int SHOOT_BUTTON_WIDTH = 50;
    private static int SHOOT_BUTTON_HEIGHT = 50;
    private static int SHOOT_BUTTON_EDGE_OFFSET = 35;

    private SharedPreferences.Editor _editor;
    private int _maxDistanceTraveled;
    private DisplayMetrics _metrics;
    Rect _shootButton;

    @SuppressLint("CommitPrefEdits")
    UI(Game game, Context context) {
        loadResources(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        _editor = prefs.edit();
        _metrics = context.getResources().getDisplayMetrics();
        _shootButton = new Rect(
                game.STAGE_WIDTH - (SHOOT_BUTTON_WIDTH + SHOOT_BUTTON_EDGE_OFFSET),
                game.STAGE_HEIGHT - (SHOOT_BUTTON_HEIGHT + SHOOT_BUTTON_EDGE_OFFSET),
                game.STAGE_WIDTH - SHOOT_BUTTON_EDGE_OFFSET,
                game.STAGE_HEIGHT - SHOOT_BUTTON_EDGE_OFFSET);
        _maxDistanceTraveled = prefs.getInt(LONGEST_DIST, 0);
    }

    private void loadResources(Context context) {
        try {
            GAME_OVER = context.getResources().getString(R.string.game_over_message);
            RESTART_MESSAGE = context.getResources().getString(R.string.restart_message);
            HEALTH = context.getResources().getString(R.string.health_text);
            DISTANCE = context.getResources().getString(R.string.distance_text);
            SHOOT_BUTTON_WIDTH = context.getResources().getInteger(R.integer.shoot_button_width);
            SHOOT_BUTTON_HEIGHT = context.getResources().getInteger(R.integer.shoot_button_height);
            SHOOT_BUTTON_EDGE_OFFSET = context.getResources().getInteger(R.integer.shoot_button_edge_offset);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    boolean shootButtonHitBox(float x, float y, float stage_width, float stage_height) {
        x = convertToStageWidth(x, stage_width);
        y = convertToStageHeight(y, stage_height);
        return (x < stage_width - (SHOOT_BUTTON_EDGE_OFFSET) &&
                x > stage_width - (SHOOT_BUTTON_WIDTH + SHOOT_BUTTON_EDGE_OFFSET) &&
                y < stage_height - SHOOT_BUTTON_EDGE_OFFSET &&
                y > stage_height- (SHOOT_BUTTON_HEIGHT + SHOOT_BUTTON_EDGE_OFFSET));
    }

    private float convertToStageWidth(float x, float width){
        return width * (x / _metrics.widthPixels);
    }

    private float convertToStageHeight(float y, float height){
        return height * (y / _metrics.heightPixels);
    }

    void saveHighScore(int distanceTraveled) {
        if (distanceTraveled > _maxDistanceTraveled) {
            _maxDistanceTraveled = distanceTraveled;
            _editor.putInt(LONGEST_DIST, _maxDistanceTraveled);
            _editor.apply();
        }
    }
}
