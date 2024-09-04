package com.kaliostech.spacexo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.kaliostech.spacexo.databinding.ActivityPlayerChoiceBinding;
import com.kaliostech.spacexo.soundmanager.SoundManager;

public class PlayerChoice extends AppCompatActivity {

    private SoundManager soundManager;

    private ActivityPlayerChoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        soundManager = new SoundManager(this, R.raw.buttonclick);

        binding.singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundManager.playSound();
                Intent intent = new Intent(PlayerChoice.this, AddPlayer.class);
                intent.putExtra("singlePlayer", true);
                startActivity(intent);
            }
        });
        binding.PlaywithFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundManager.playSound();
                Intent intent = new Intent(PlayerChoice.this, AddPlayer.class);
                intent.putExtra("singlePlayer", false);
                startActivity(intent);
            }
        });
    }
}
