package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class Game extends SurfaceView implements Runnable {

    public static final String TAG = "Game";
    public static final String PREFS = "com.viktorvilmusenaho.spaceshooter";
    public static final String LONGEST_DIST = "longest_distance";
    static final int STAGE_WIDTH = 1280; // TODO: Move game setting to resources
    static final int STAGE_HEIGHT = 720;
    static final int STAR_COUNT = 40;
    static final int ENEMY_COUNT = 8;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private SurfaceHolder _holder = null;
    private Paint _paint = new Paint();
    private Canvas _canvas = null;

    private ArrayList<Entity> _entities = new ArrayList<Entity>();
    private Player _player;
    Random _rng = new Random();
    private JukeBox _jukebox = null;
    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;


    volatile boolean _isBoosting = false;
    float _playerSpeed = 0f;
    int _distanceTraveled = 0;
    int _maxDistanceTraveled = 0;
    private boolean _gameOver = false;

    public Game(Context context) {
        super(context);
        Entity._game = this;
        _holder = getHolder();
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _jukebox = new JukeBox(context);

        _prefs = context.getSharedPreferences(PREFS, context.MODE_PRIVATE);
        _editor = _prefs.edit();

        //TODO Separate
        for (int i = 0; i < STAR_COUNT; i++) {
            _entities.add(new Star());
        }
        for (int i = 0; i < ENEMY_COUNT; i++) {
            _entities.add(new Enemy());
        }
        _player = new Player();
        restart();
    }

    private void restart(){
        for (Entity e : _entities) {
            e.respawn();
        }
        _player.respawn();
        _distanceTraveled = 0;
        _maxDistanceTraveled = _prefs.getInt(LONGEST_DIST, 0);
        _gameOver = false;
        // TODO sound effect for starting game
    }

    @Override
    public void run() {
        while (_isRunning) {
            update();
            render();
        }
    }


    private void update() {
        if(_gameOver){
            return;
        }
        _player.update();
        for (Entity e : _entities) {
            e.update();
        }
        _distanceTraveled += _playerSpeed;
        checkCollisions();
        checkGameOver();
    }

    private void checkGameOver() {
        if(_player._health < 0){
            _gameOver = true;
            if(_distanceTraveled > _maxDistanceTraveled){
                _maxDistanceTraveled = _distanceTraveled;
                _editor.putInt(LONGEST_DIST, _maxDistanceTraveled);
                _editor.apply();
            }
            // TODO sound effect game over
        }
    }

    private void checkCollisions() {
        Entity temp = null;
        for (int i = STAR_COUNT; i < _entities.size(); i++) { // 0 - STAR_COUNT == background entities TODO separate maybe
            temp = _entities.get(i);
            if(_player.isColliding(temp)){
                _player.onCollision(temp);
                temp.onCollision(_player);
                _jukebox.play(JukeBox.CRASH);
            }
        }
    }

    private void render() {
        if (!acquireAndLockCanvas()) {
            return;
        }

        _canvas.drawColor(Color.BLACK);

        for (Entity e : _entities) {
            e.render(_canvas, _paint);
        }
        _player.render(_canvas, _paint);
        renderHUD(_canvas, _paint);
        _holder.unlockCanvasAndPost(_canvas);
    }

    private void renderHUD(final Canvas canvas, final Paint paint){
        float textSize = 48f;
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
        if(!_gameOver) {
            canvas.drawText("Health: " + _player._health, 10, 48, paint); //TODO resource
            canvas.drawText("Distance: " + _distanceTraveled, 10, textSize*2, paint);
        } else {
            final float centerY = STAGE_HEIGHT/2;
            canvas.drawText("GAME OVER!", STAGE_WIDTH/2, centerY, paint); //TODO resource
            canvas.drawText("(press to restart)", STAGE_WIDTH/2, centerY + textSize, paint); //TODO resource
        }
    }

    private boolean acquireAndLockCanvas() {
        if (!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _gameThread = new Thread(this);
        _gameThread.start();
    }

    protected void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        try {
            _gameThread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, Log.getStackTraceString(e.getCause()));
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;

        for (Entity e : _entities) {
            e.destroy();
        }
        _jukebox.destroy();
        Entity._game = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP: //finger lifted
                _isBoosting = false;
                if(_gameOver){
                    restart();
                }
                break;
            case MotionEvent.ACTION_DOWN: //finger pressed
                _isBoosting = true;
                break;
        }
        return true;
    }
}
