package com.kaliostech.spacexo.ui

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.kaliostech.spacexo.R

class PlayerChoice : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private var clickSoundId: Int = 0
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_choice)

        //background music
        // Initialize and start background music
        mediaPlayer = MediaPlayer.create(this, R.raw.initbackgroundmusic)
        mediaPlayer.isLooping = true // Set looping
        mediaPlayer.start()


        // Initialize SoundPool with attributes
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Load the click sound
        clickSoundId = soundPool.load(this, R.raw.buttonclick, 1)

        // Set up onClickListeners with sound and intent
        findViewById<ImageView>(R.id.singleplayer).setOnClickListener {
            playClickSound()
            // Launch the new activity with isSingleplayer set to true
            startGameActivity(isSingleplayer = true)
        }

        findViewById<ImageView>(R.id.twoplayers).setOnClickListener {
            playClickSound()
            // Launch the new activity with isSingleplayer set to false
            startGameActivity(isSingleplayer = false)
        }
    }

    private fun playClickSound() {
        soundPool.play(clickSoundId, 1f, 1f, 1, 0, 1f)
    }

    private fun startGameActivity(isSingleplayer: Boolean) {
        val intent = Intent(this, AddPlayer::class.java).apply {
            putExtra("isSingleplayer", isSingleplayer)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
        // Release the media player resources
        mediaPlayer.release()
    }
    override fun onPause() {
        super.onPause()
        // Stop the music when the activity is not in the foreground
        mediaPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume music playback if it was paused
        mediaPlayer.start()
    }
}
