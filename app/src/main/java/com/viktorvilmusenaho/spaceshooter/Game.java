package com.viktorvilmusenaho.spaceshooter;

import android.annotation.SuppressLint;
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
    static final int STAGE_WIDTH = 1280; // TODO: Move game setting to resources
    static final int STAGE_HEIGHT = 720;
    static final int STAR_COUNT = 60;
    static final int METEOR_COUNT = 5;
    static final int ENEMY_COUNT = 8;
    static final float TEXT_SIZE = 48f;

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private SurfaceHolder _holder = null;
    private Paint _paint = new Paint();
    private Canvas _canvas = null;

    private ArrayList<Entity> _collidableEntities = new ArrayList<Entity>();
    private ArrayList<Entity> _backgroundEntities = new ArrayList<Entity>();
    private Player _player;
    Random _rng = new Random();
    UI _ui;
    private JukeBox _jukebox = null;

    volatile boolean _isBoosting = false;
    float _playerSpeed = 0f;
    int _distanceTraveled = 0;
    private boolean _gameOver = false;

    public Game(Context context) {
        super(context);
        Entity._game = this;
        _ui = new UI(context);
        _holder = getHolder();
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _jukebox = new JukeBox(context);
        populateEntities();
        restart();
    }

    private void populateEntities() {
        for (int i = 0; i < STAR_COUNT; i++) {
            _backgroundEntities.add(new Star());
        }
        for (int i = 0; i < ENEMY_COUNT; i++) {
            _collidableEntities.add(new Enemy());
        }
        for (int i = 0; i < METEOR_COUNT; i++) {
            _collidableEntities.add(new EnemyMeteor());
        }
        _player = new Player();
    }

    private void restart() {
        for (Entity e : _backgroundEntities) {
            e.respawn();
        }
        for (Entity e : _collidableEntities) {
            e.respawn();
        }
        _player.respawn();
        _distanceTraveled = 0;
        _gameOver = false;
        _jukebox.play(_jukebox.GAME_START);
    }

    @Override
    public void run() {
        while (_isRunning) {
            update();
            render();
        }
    }

    private void update() {
        if (_gameOver) {
            return;
        }
        _player.update();
        for (Entity e : _backgroundEntities) {
            e.update();
        }
        for (Entity e : _collidableEntities) {
            e.update();
        }
        _distanceTraveled += _playerSpeed;
        checkCollisions();
        checkGameOver();
    }

    private void checkGameOver() {
        if (_player._health < 0) {
            _gameOver = true;
            _ui.saveHighScore(_distanceTraveled);
            _jukebox.play(_jukebox.GAME_OVER);
        }
    }

    private void checkCollisions() {
        Entity temp = null;
        for (int i = 0; i < _collidableEntities.size(); i++) {
            temp = _collidableEntities.get(i);
            if (_player.isColliding(temp) && _player._graceCounter == 0) {
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

        for (Entity e : _backgroundEntities) {
            e.render(_canvas, _paint);
        }
        for (Entity e : _collidableEntities) {
            e.render(_canvas, _paint);
        }
        if (_player._graceCounter % 2 != 1) { // "blink" every other frame during players grace period
            _player.render(_canvas, _paint);
        }
        renderHUD(_canvas, _paint);
        _holder.unlockCanvasAndPost(_canvas);
    }

    @SuppressLint("DefaultLocale")
    private void renderHUD(final Canvas canvas, final Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(TEXT_SIZE);
        if (!_gameOver) {
            canvas.drawText(String.format("%s%d", UI.HEALTH, _player._health), 10, TEXT_SIZE, paint);
            canvas.drawText(String.format("%s%d", UI.DISTANCE, _distanceTraveled), 10, TEXT_SIZE * 2, paint);
        } else {
            final float centerY = STAGE_HEIGHT / 2;
            canvas.drawText(UI.GAME_OVER, STAGE_WIDTH / 2, centerY, paint);
            canvas.drawText(UI.RESTART_MESSAGE, STAGE_WIDTH / 2, centerY + TEXT_SIZE, paint);
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

        for (Entity e : _backgroundEntities) {
            e.destroy();
        }
        for (Entity e : _collidableEntities) {
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
                if (_gameOver) {
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
