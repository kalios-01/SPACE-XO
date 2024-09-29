package com.kaliostech.spacexo.soundmanager;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    private MediaPlayer mediaPlayer;

    public SoundManager(Context context, int soundResId) {
        mediaPlayer = MediaPlayer.create(context, soundResId);
    }

    public void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
    }
}

