package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
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
    static final int STAGE_WIDTH = 1280; // TODO: Move game setting to reqources
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

    volatile boolean _isBoosting = false;
    float _playerSpeed = 0f;

    public Game(Context context) {
        super(context);
        Entity._game = this;
        _holder = getHolder();
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);

        //TODO Separate
        for (int i = 0; i < STAR_COUNT; i++) {
            _entities.add(new Star());
        }
        for (int i = 0; i < ENEMY_COUNT; i++) {
            _entities.add(new Enemy());
        }
        _player = new Player();
    }

    @Override
    public void run() {
        while (_isRunning) {
            input();
            update();
            render();
        }
        //input
        //update
        //render
    }

    private void input() {

    }

    private void update() {
        _player.update();
        for (Entity e : _entities) {
            e.update();
        }
        for (int i = STAR_COUNT; i < STAR_COUNT; i++) {
            _entities.add(new Star());
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
        //render each entity
        //render the HUD

        _holder.unlockCanvasAndPost(_canvas);
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

        Entity._game = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP: //finger lifted
                _isBoosting = false;
                break;
            case MotionEvent.ACTION_DOWN: //finger pressed
                _isBoosting = true;
                break;
        }
        return true;
    }
}
