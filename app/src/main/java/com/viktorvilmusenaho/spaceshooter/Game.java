package com.viktorvilmusenaho.spaceshooter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.sax.EndTextElementListener;
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

    private Thread _gameThread = null;
    private volatile boolean _isRunning = false;
    private SurfaceHolder _holder = null;
    private Paint _paint = new Paint();
    private Canvas _canvas = null;

    private ArrayList<Entity> _backgroundEntities = new ArrayList<>();
    public ArrayList<Entity> _collidableEntities = new ArrayList<>();
    public Player _player;
    Random _rng = new Random();
    UI _ui = null;
    private JukeBox _jukebox = null;
    private Context _context = null;

    volatile boolean _isBoosting = false;
    float _playerSpeed = 0f;
    int _distanceTraveled = 0;
    public boolean _gameOver = false;

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
        _player = new Player(_context);
        for (int i = 0; i < STAR_COUNT; i++) {
            _backgroundEntities.add(new Star(_context));
        }
        for (int i = 0; i < ENEMY_COUNT; i++) {
            _collidableEntities.add(new Enemy(_context));
        }
        for (int i = 0; i < METEOR_COUNT; i++) {
            _collidableEntities.add(new EnemyMeteor(_context));
        }
        for (int i = 0; i < MAX_SHOTS_ONSCREEN; i++) {
            _collidableEntities.add(new PlayerProjectile(_context, _player._width, _player._height));
        }
        for (int i = 0; i < POWER_UP_COUNT; i++) {
            _collidableEntities.add(new PowerUp(_context));
        }
    }

    private void restart() {
        for (Entity e : _collidableEntities) {
            e.respawn();
        }
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

        _distanceTraveled += _playerSpeed;
        checkCollisions();
        checkGameOver();
    }

    private void checkGameOver() {
        if (_player._health <= 0) {
            _gameOver = true;
            _ui.saveHighScore(_distanceTraveled);
            _jukebox.play(JukeBox.GAME_OVER);
        }
    }

    private void checkCollisions() {
        Entity temp = null;
        for (int i = 0; i < _collidableEntities.size(); i++) {
            temp = _collidableEntities.get(i);
            checkPlayerCollisions(temp);
            checkAllCollisions(temp);
        }
    }

    private void checkPlayerCollisions(Entity e) {
        if (_player.isColliding(e)) {
            if (e instanceof PlayerProjectile) {
                return;
            } else if (e instanceof PowerUp) {
                collision(_player, e);
            } else if ((e instanceof Enemy || e instanceof EnemyMeteor) && _player._graceCounter == 0) {
                collision(_player, e);
            }
        }
    }

    private void checkAllCollisions(Entity e) {
        for (Entity temp : _collidableEntities) {
            if (temp != e && temp.isColliding(e)) {
                if (e instanceof PlayerProjectile && (temp instanceof Enemy || temp instanceof EnemyMeteor || temp instanceof PowerUp)) {
                    collision(temp, e);
                } else if (e instanceof Enemy && temp instanceof PlayerProjectile) {
                    collision(temp, e);
                } else if (e instanceof EnemyMeteor && temp instanceof PlayerProjectile) {
                    collision(temp, e);
                } else if (e instanceof PowerUp && temp instanceof PlayerProjectile) {
                    collision(temp, e);
                }

            }
        }
    }

    private void collision(Entity a, Entity b) {
        a.onCollision(b);
        b.onCollision(a);
        _jukebox.play(JukeBox.CRASH);
    }

    public void killAllEnemies() {
        for (Entity e : _collidableEntities) {
            if (!(e instanceof PlayerProjectile)) {
                e.respawn();
            }
        }
    }

    public int shotsOnScreen(){
        int count = 0;
        for (Entity e : _collidableEntities) {
            if (e instanceof PlayerProjectile && ((PlayerProjectile) e)._isActive) {
                count++;
            }
        }
        return count;
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

        _ui.render(_canvas, _paint);
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

        for (Entity e : _backgroundEntities) {
            e.destroy();
        }
        for (Entity e : _collidableEntities) {
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

    private void checkPress(float x, float y) {
        if (_ui.shootButtonHitBox(x, y, STAGE_WIDTH, STAGE_HEIGHT)) {
            PlayerProjectile shot = findInactiveProjectile();
            if (shot != null) {
                shot._isActive = true;
                shot.spawn(_player._x, _player._y);
                _jukebox.play(JukeBox.PLAYER_SHOOT);
            }
        } else {
            _isBoosting = true;
        }
    }

    private PlayerProjectile findInactiveProjectile() {
        for (Entity e : _collidableEntities) {
            if (e instanceof PlayerProjectile) {
                PlayerProjectile temp = (PlayerProjectile) e;
                if (!temp._isActive) {
                    return temp;
                }
            }
        }
        return null;
    }
}
