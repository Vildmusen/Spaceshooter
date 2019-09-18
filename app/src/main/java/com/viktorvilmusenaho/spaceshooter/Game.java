package com.viktorvilmusenaho.spaceshooter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class Game extends SurfaceView implements Runnable {

    public static final String TAG = "Game";
    final int STAGE_WIDTH = getResources().getInteger(R.integer.stage_width);
    final int STAGE_HEIGHT = getResources().getInteger(R.integer.stage_height);
    final int STAR_COUNT = getResources().getInteger(R.integer.star_count);
    final int METEOR_COUNT = getResources().getInteger(R.integer.meteor_count);
    final int ENEMY_COUNT = getResources().getInteger(R.integer.enemy_count);
    final int POWER_UP_COUNT = getContext().getResources().getInteger(R.integer.power_up_count);
    final int MAX_SHOTS_ONSCREEN = getResources().getInteger(R.integer.projectile_max_count);
    final int SHOT_TOOLTIP_SIZE = 40;
    final int SHOT_TOOLTIP_OFFSET = 30;
    final float TEXT_SIZE = (float) getResources().getInteger(R.integer.text_size);

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private SurfaceHolder _holder = null;
    private Paint _paint = new Paint();
    private Canvas _canvas = null;

    private ArrayList<Entity> _collidableEntities = new ArrayList<>();
    private ArrayList<Entity> _backgroundEntities = new ArrayList<>();
    private ArrayList<Entity> _projectileEntities = new ArrayList<>();
    private Player _player;
    Random _rng = new Random();
    UI _ui = null;
    private JukeBox _jukebox = null;
    private Context _context = null;

    volatile boolean _isBoosting = false;
    float _playerSpeed = 0f;
    int _distanceTraveled = 0;
    private boolean _gameOver = false;

    public Game(Context context) {
        super(context);
        _context = context;
        Entity._game = this;
        _ui = new UI(this, context);
        _holder = getHolder();
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _jukebox = new JukeBox(context);
        populateEntities();
        restart();
    }

    private void populateEntities() {
        for (int i = 0; i < STAR_COUNT; i++) {
            _backgroundEntities.add(new Star(_context));
        }
        for (int i = 0; i < ENEMY_COUNT; i++) {
            _collidableEntities.add(new Enemy(_context));
        }
        for (int i = 0; i < METEOR_COUNT; i++) {
            _collidableEntities.add(new EnemyMeteor(_context));
        }
        for (int i = 0; i < POWER_UP_COUNT; i++) {
            _collidableEntities.add(new PowerUp(_context));
        }
        _player = new Player(_context);
    }

    private void restart() {
        for (Entity e : _backgroundEntities) {
            e.respawn();
        }
        for (Entity e : _collidableEntities) {
            e.respawn();
        }
        _projectileEntities = new ArrayList<>();
        _player.respawn();
        _distanceTraveled = 0;
        _gameOver = false;
        _jukebox.play(JukeBox.GAME_START);
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

        int toRemove = -1;
        for (int i = 0; i < _projectileEntities.size(); i++) {
            PlayerProjectile temp = (PlayerProjectile) _projectileEntities.get(i);
            if (temp.isOnScreen()) {
                temp.update();
            } else {
                toRemove = i;
                break;
            }
        }
        if (toRemove != -1) {
            _projectileEntities.remove(toRemove);
        }

        _distanceTraveled += _playerSpeed;
        checkCollisions();
        checkGameOver();
    }

    private void checkGameOver() {
        if (_player._health < 0) {
            _gameOver = true;
            _ui.saveHighScore(_distanceTraveled);
            _jukebox.play(JukeBox.GAME_OVER);
        }
    }

    private void checkCollisions() {
        Entity temp;
        for (int i = 0; i < _collidableEntities.size(); i++) {
            temp = _collidableEntities.get(i);
            checkPlayerColliding(temp);
            checkShotsColliding(temp);
        }
    }

    private void checkPlayerColliding(Entity enemy) {
        if (_player.isColliding(enemy) && _player._graceCounter == 0) {
            _player.onCollision(enemy);
            enemy.onCollision(_player);
            _jukebox.play(JukeBox.CRASH);
        }
    }

    private void checkShotsColliding(Entity enemy) {
        Entity temp;
        for (int i = 0; i < _projectileEntities.size(); i++) {
            temp = _projectileEntities.get(i);
            if (enemy.isColliding(temp)) {
                enemy.onCollision(temp);
                temp.onCollision(enemy);
                _projectileEntities.remove(temp);
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
        for (Entity e : _projectileEntities) {
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
            paint.setColor(Color.RED);
            canvas.drawRect(_ui._shootButton, paint);
            for(int i = 0; i < MAX_SHOTS_ONSCREEN - _projectileEntities.size() - 1; i++){
                canvas.drawRect(new Rect(
                        (STAGE_WIDTH - SHOT_TOOLTIP_SIZE + SHOT_TOOLTIP_OFFSET) - (i*SHOT_TOOLTIP_OFFSET),
                        SHOT_TOOLTIP_SIZE,
                        (STAGE_WIDTH - SHOT_TOOLTIP_OFFSET) - (i*SHOT_TOOLTIP_OFFSET),
                        SHOT_TOOLTIP_OFFSET),
                        paint);
            }
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
        for (Entity e : _projectileEntities){
            e.destroy();
        }
        _jukebox.destroy();
        Entity._game = null;
    }

    @SuppressLint("ClickableViewAccessibility")
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
                checkPress(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //ANOTHER finger pressed
                int index = event.getActionIndex();
                checkPress(event.getX(index), event.getY(index));
                break;
        }
        return true;
    }

    private void checkPress(float x, float y){
        if (_ui.shootButtonHitBox(x, y, STAGE_WIDTH, STAGE_HEIGHT)) {
            if (_projectileEntities.size() + 1 < MAX_SHOTS_ONSCREEN) {
                _projectileEntities.add(new PlayerProjectile(_context, _player._x, _player._y, _player._width, _player._height));
                _jukebox.play(JukeBox.PLAYER_SHOOT);
            }
        } else {
            _isBoosting = true;
        }
    }
}
