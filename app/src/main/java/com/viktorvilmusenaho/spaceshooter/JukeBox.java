package com.viktorvilmusenaho.spaceshooter;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.io.IOException;

public class JukeBox {

    private static final int MAX_STREAMS = 3;
    static int CRASH = 0;
    static int GAME_START = 0;
    static int GAME_OVER = 0;
    static int PLAYER_SHOOT = 0;
    static int POWER_UP = 0;

    private SoundPool _soundPool;

    JukeBox(final Context context) {
        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        _soundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(MAX_STREAMS)
                .build();
        loadSounds(context);
    }

    private void loadSounds(final Context context) {
        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("crash.wav");
            CRASH = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("game_over.wav");
            GAME_OVER = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("game_start.ogg");
            GAME_START = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("laser_pew.wav");
            PLAYER_SHOOT = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("game_start.wav");
            POWER_UP = _soundPool.load(descriptor, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void play(final int soundID) {
        final float leftVolume = 1f;
        final float rightVolume = 1f;
        final int priority = 1;
        final int loop = 0;
        final float rate = 1.0f;

        if (soundID > 0) {
            _soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    void destroy() {
        _soundPool.release();
        _soundPool = null;
    }
}
