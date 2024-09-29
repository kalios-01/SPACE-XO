package com.kaliostech.spacexo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kaliostech.spacexo.soundmanager.SoundManager;

public class AddPlayer extends AppCompatActivity {

    private SoundManager soundManager;
    private boolean isPlaying;  // Track current sound state


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);
        EditText playerOne = findViewById(R.id.playerOne);
        EditText playerTwo = findViewById(R.id.playerTwo);
        ImageView startGameButton = findViewById(R.id.startGameButton);

        soundManager = new SoundManager(this, R.raw.buttonclick);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.login);
        mediaPlayer.setLooping(true); // Set to loop indefinitely
        mediaPlayer.setVolume(0.2f, 0.2f);
        mediaPlayer.start();
        // Using the interface
        PreferenceHelper preferenceHelper = new SharedPreferencesManager(this);
        isPlaying = preferenceHelper.getIsPlaying();


        // bot implementation for 1 player
        boolean isSinglePlayer = getIntent().getBooleanExtra("singlePlayer", false);
        if (isSinglePlayer){
            playerTwo.setText("AI");
            playerTwo.setEnabled(false);
        }

        startGameButton.setOnClickListener(new View.OnClickListener() {
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
                if (!isSinglePlayer){
                    String getPlayerOneName = playerOne.getText().toString();
                    String getPlayerTwoName = playerTwo.getText().toString();
                    if (getPlayerOneName.isEmpty() || getPlayerTwoName.isEmpty()) {
                        Toast.makeText(AddPlayer.this, "Please enter player name", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AddPlayer.this, MainActivity.class);
                        intent.putExtra("playerOne", getPlayerOneName);
                        intent.putExtra("playerTwo", getPlayerTwoName);
                        startActivity(intent);
                        finish();
                    }

                }else{
                    String getPlayerTwoName = "AI";
                    String getPlayerOneName = playerOne.getText().toString();
                    if (getPlayerOneName.isEmpty()) {
                        Toast.makeText(AddPlayer.this, "Please enter player name", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AddPlayer.this, MainActivity.class);
                        intent.putExtra("playerOne", getPlayerOneName);
                        intent.putExtra("playerTwo", getPlayerTwoName);
                        intent.putExtra("singlePlayer", true);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}
