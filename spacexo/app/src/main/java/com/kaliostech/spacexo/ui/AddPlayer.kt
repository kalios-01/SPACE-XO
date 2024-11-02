package com.kaliostech.spacexo.ui

import android.content.Intent
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kaliostech.spacexo.R

class AddPlayer : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_player)

        // Initialize media player for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.initbackgroundmusic)
        mediaPlayer?.isLooping = true // Set looping
        mediaPlayer?.start()

        // Initialize sound pool for click sound
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        clickSoundId = soundPool.load(this, R.raw.buttonclick, 1) // Replace with your click sound file

        // Get the isSingleplayer value from intent
        val isSingleplayer = intent.getBooleanExtra("isSingleplayer", true)

        // Find EditTexts
        val playerOneEditText = findViewById<EditText>(R.id.playerOne)
        val playerTwoEditText = findViewById<EditText>(R.id.playerTwo)
        val startGameButton = findViewById<ImageView>(R.id.startGameButton)

        if (isSingleplayer) {
            // Make playerOne EditText uneditable and set text to "AI"
            playerTwoEditText.setText("AI")
            playerTwoEditText.isEnabled = false
        } else {
            // Enable playerTwo EditText
            playerTwoEditText.isEnabled = true
        }

        // Set click listener for the start game button
        startGameButton.setOnClickListener {
            // Play click sound
            soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)

            // Get player names
            val playerOneName = playerOneEditText.text.toString()
            val playerTwoName = playerTwoEditText.text.toString()
            // Check if player names are empty
            if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                Toast.makeText(this, "Please enter player names.", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with your logic (e.g., starting the game)
                // Pass player names and isSingleplayer variable to GameActivity
                val intent = Intent(this, GameScreen::class.java)
                intent.putExtra("playerOneName", playerOneName)
                intent.putExtra("playerTwoName", playerTwoName)
                intent.putExtra("isSingleplayer", isSingleplayer)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause music when activity is not in the foreground
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume music playback if it was paused
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release the media player and sound pool resources
        mediaPlayer?.release()
        mediaPlayer = null
        soundPool.release()
    }
}
