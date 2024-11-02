package com.kaliostech.spacexo.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kaliostech.spacexo.R
import com.kaliostech.spacexo.ai.Bot
import com.kaliostech.spacexo.databinding.ActivityGamescreenBinding

class GameScreen : AppCompatActivity() {

    private lateinit var binding: ActivityGamescreenBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var soundPool: SoundPool
    private var moveSoundId: Int = 0
    private var buttonSoundId: Int = 0
    private var isSoundOn: Boolean = true // Sound starts as on
    private lateinit var audioManager: AudioManager

    // main game variables
    private val combinationList = mutableListOf<IntArray>()
    private var boxPositions = IntArray(9) { 0 } // 9 zeros
    private var playerTurn = 1
    private var totalSelectedBoxes = 0
    private lateinit var bot: Bot
    private var isSinglePlayer = false
    private var isUserTurn = true
    private var playerOneScore = 0
    private var playerTwoScore = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the binding
        binding = ActivityGamescreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bot = Bot()

        // Initialize media player for background music
        mediaPlayer = MediaPlayer.create(this, R.raw.gamescreenbackgroundmusic)
        mediaPlayer?.isLooping = true // Set looping
        mediaPlayer?.start()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Initialize sound pool for click sounds
        soundPool = SoundPool.Builder()
            .setMaxStreams(2) // Allowing two simultaneous sounds
            .build()

        // Load sounds
        moveSoundId = soundPool.load(this, R.raw.movessound, 1) // Sound for player moves
        buttonSoundId = soundPool.load(this, R.raw.buttonclick, 1) // Sound for dialog button clicks

        // Receive intent data
        isSinglePlayer = intent.getBooleanExtra("isSingleplayer", false)
        val playerOneName = intent.getStringExtra("playerOneName")
        val playerTwoName = intent.getStringExtra("playerTwoName")

        binding.playerOneName.text = playerOneName
        binding.playerTwoName.text = playerTwoName

        binding.menu.setOnClickListener {
            playButtonSound()
            openPauseGameMenu()
        }
        binding.playeronescore.text = "Score : $playerOneScore"
        binding.playertwoscore.text = "Score : $playerTwoScore"


        // Initialize combinationList
        with(combinationList) {
            add(intArrayOf(0, 1, 2))
            add(intArrayOf(3, 4, 5))
            add(intArrayOf(6, 7, 8))
            add(intArrayOf(0, 3, 6))
            add(intArrayOf(1, 4, 7))
            add(intArrayOf(2, 5, 8))
            add(intArrayOf(2, 4, 6))
            add(intArrayOf(0, 4, 8))
        }
        // Set click listeners
        with(binding) {
            image1.setOnClickListener(createClickListener(0))
            image2.setOnClickListener(createClickListener(1))
            image3.setOnClickListener(createClickListener(2))
            image4.setOnClickListener(createClickListener(3))
            image5.setOnClickListener(createClickListener(4))
            image6.setOnClickListener(createClickListener(5))
            image7.setOnClickListener(createClickListener(6))
            image8.setOnClickListener(createClickListener(7))
            image9.setOnClickListener(createClickListener(8))
        }
    }

    // oncreate over here
    // game logic
    private fun createClickListener(position: Int) = View.OnClickListener { view ->
        if (isUserTurn && isBoxSelectable(position)) {
            performAction(view as ImageView, position)
        }
    }

    private fun performAction(imageView: ImageView, selectedBoxPosition: Int) {
        boxPositions[selectedBoxPosition] = playerTurn

        when (playerTurn) {
            1 -> {
                imageView.setImageResource(R.drawable.x_image)
                playMoveSound()
                totalSelectedBoxes++ // Increment the total moves

                if (checkResults()) {
                    showResult(binding.playerOneName.text.toString())
                } else if (isDraw()) { // Check for draw
                    showResult("Match Draw")
                } else {
                    changePlayerTurn(2)
                    if (isSinglePlayer) {
                        isUserTurn = false
                        botMove()
                    }
                }
            }

            else -> {
                imageView.setImageResource(R.drawable.o_image)
                playMoveSound()
                totalSelectedBoxes++ // Increment the total moves

                if (checkResults()) {
                    showResult(binding.playerTwoName.text.toString())
                } else if (isDraw()) { // Check for draw
                    showResult("Match Draw")
                } else {
                    changePlayerTurn(1)
                }
            }
        }
    }
    private fun showResult(winner: String) {
        // Create the Dialog instance
        val dialog = Dialog(this).apply {
            setContentView(R.layout.activity_result_dialog)
            setCancelable(false)
        }
        // Set the message text
        val messageText: TextView = dialog.findViewById(R.id.messageText)
        messageText.text = winner

        // Update scores
        if (winner == binding.playerOneName.text.toString()) {
            playerOneScore++
        } else if (winner == binding.playerTwoName.text.toString()) {
            playerTwoScore++
        }

        // Update the score TextViews
        binding.playeronescore.text = "Score : $playerOneScore"
        binding.playertwoscore.text = "Score : $playerTwoScore"

        // Set up the Start Again button click listener
        val startAgainButton: ImageView = dialog.findViewById(R.id.startAgainButton)
        startAgainButton.setOnClickListener {
            restartMatch()
            playButtonSound()
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun isDraw(): Boolean {
        return totalSelectedBoxes == 9 && !checkResults()
    }

    private fun changePlayerTurn(currentPlayerTurn: Int) {
        playerTurn = currentPlayerTurn
        if (playerTurn == 1) {
            binding.playerX.setBackgroundResource(R.drawable.currentplayer_highlight)
            binding.playerO.setBackgroundResource(R.drawable.white_box)
        } else {
            binding.playerX.setBackgroundResource(R.drawable.white_box)
            binding.playerO.setBackgroundResource(R.drawable.currentplayer_highlight)
        }
    }

    private fun checkResults(): Boolean {
        for (combination in combinationList) {
            if (boxPositions[combination[0]] == playerTurn &&
                boxPositions[combination[1]] == playerTurn &&
                boxPositions[combination[2]] == playerTurn
            ) {
                return true
            }
        }
        return false
    }

    private fun isBoxSelectable(boxPosition: Int): Boolean {
        return boxPositions[boxPosition] == 0
    }

    private fun botMove() {
        // Check if the game has already ended (win or draw)
        if (checkResults()) {
            return // Exit the method if there is already a result
        }

        // Check if there are any moves left for the bot
        val movesLeft = boxPositions.any { it == 0 }

        if (!movesLeft) {
            showResult("Match Draw")
            restartMatch()
            return
        }

        // Delay the bot's move by 1 second (1000 milliseconds)
        android.os.Handler().postDelayed({
            // Ensure no result has been declared between the delay
            if (checkResults()) {
                return@postDelayed // If the user won during this delay, exit
            }

            val move = bot.getMove(boxPositions)
            val botImageView = getImageViewForPosition(move)
            if (botImageView != null) {
                performAction(botImageView, move)
            }

            // After bot's move, check again if the game is over
            if (checkResults()) {
                return@postDelayed // Exit if the bot's move caused a win or draw
            }

            isUserTurn = true // Allow the user to play after the bot's move
        }, 1000) // 1000 milliseconds delay
    }


    private fun getImageViewForPosition(position: Int): ImageView? {
        return when (position) {
            0 -> binding.image1
            1 -> binding.image2
            2 -> binding.image3
            3 -> binding.image4
            4 -> binding.image5
            5 -> binding.image6
            6 -> binding.image7
            7 -> binding.image8
            8 -> binding.image9
            else -> null
        }
    }

    private fun restartMatch() {
        boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0) // 9 zeros
        playerTurn = 1
        isUserTurn = true
        totalSelectedBoxes = 0

        // Reset images to white box
        binding.image1.setImageResource(R.drawable.white_box)
        binding.image2.setImageResource(R.drawable.white_box)
        binding.image3.setImageResource(R.drawable.white_box)
        binding.image4.setImageResource(R.drawable.white_box)
        binding.image5.setImageResource(R.drawable.white_box)
        binding.image6.setImageResource(R.drawable.white_box)
        binding.image7.setImageResource(R.drawable.white_box)
        binding.image8.setImageResource(R.drawable.white_box)
        binding.image9.setImageResource(R.drawable.white_box)
    }

    // Game logic ends here
    private fun openPauseGameMenu() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_pause_game_menu, null)
        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // Get initial volume state
        val isSoundOn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0
        val soundToggle = dialogView.findViewById<ImageView>(R.id.sound)
        soundToggle.setImageResource(
            if (isSoundOn) R.drawable.soundon
            else R.drawable.soundoff
        )

        // Set up button listeners
        dialogView.findViewById<ImageView>(R.id.resume).setOnClickListener {
            playButtonSound()
            dialog.dismiss()
        }
        dialogView.findViewById<ImageView>(R.id.mainmenu).setOnClickListener {
            playButtonSound()
            val intent = Intent(this, PlayerChoice::class.java)
            startActivity(intent)
            finish()
        }

        dialogView.findViewById<ImageView>(R.id.exit).setOnClickListener {
            playButtonSound()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            finish()
        }
        soundToggle.setOnClickListener {
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            if (currentVolume > 0) {
                // Store the current volume before muting
                PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putInt("last_volume", currentVolume)
                    .apply()

                // Mute volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                soundToggle.setImageResource(R.drawable.soundoff)
            } else {
                // Restore to previous volume or max volume if previous not found
                val lastVolume = PreferenceManager.getDefaultSharedPreferences(this)
                    .getInt("last_volume", maxVolume)

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, lastVolume, 0)
                soundToggle.setImageResource(R.drawable.soundon)
            }
            playButtonSound()
        }

        dialog.show()
    }

    private fun playButtonSound() {
        if (isSoundOn) {
            soundPool.play(buttonSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    private fun playMoveSound() {
        if (isSoundOn) {
            soundPool.play(moveSoundId, 1f, 1f, 1, 0, 1f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        soundPool.release()
    }
}
