package com.kaliostech.spacexo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.kaliostech.spacexo.databinding.ActivityPlayerChoiceBinding;
import com.kaliostech.spacexo.soundmanager.SoundManager;

public class PlayerChoice extends AppCompatActivity {

    private SoundManager soundManager;

    private ActivityPlayerChoiceBinding binding;

    private boolean isPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // shared preferences
        PreferenceHelper preferenceHelper = new SharedPreferencesManager(this);
        isPlaying = preferenceHelper.getIsPlaying();

        soundManager = new SoundManager(this, R.raw.buttonclick);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.login);
        mediaPlayer.setLooping(true); // Set to loop indefinitely
        mediaPlayer.setVolume(0.2f, 0.2f);
        mediaPlayer.start();

        binding.singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Play the sound effect
                if (isPlaying) {
                    soundManager.playSound();
                    mediaPlayer.stop();
                }
                else {
                    soundManager.stopSound();
                    mediaPlayer.stop();
                }
                Intent intent = new Intent(PlayerChoice.this, AddPlayer.class);
                intent.putExtra("singlePlayer", true);
                startActivity(intent);
            }
        });
        binding.PlaywithFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Play the sound effect
                if (isPlaying) {
                    soundManager.playSound();
                    mediaPlayer.stop();

                }
                else {
                    soundManager.stopSound();
                    mediaPlayer.stop();

                }
                Intent intent = new Intent(PlayerChoice.this, AddPlayer.class);
                intent.putExtra("singlePlayer", false);
                startActivity(intent);
            }
        });
    }
}
