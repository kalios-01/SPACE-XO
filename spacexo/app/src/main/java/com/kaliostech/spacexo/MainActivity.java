package com.kaliostech.spacexo;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kaliostech.spacexo.databinding.ActivityMainBinding;
import com.kaliostech.spacexo.singleplayer.Bot;
import com.kaliostech.spacexo.soundmanager.SoundManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private final List<int[]> combinationList = new ArrayList<>();
    private int[] boxPositions = {0,0,0,0,0,0,0,0,0}; //9 zero
    private int playerTurn = 1;
    private int totalSelectedBoxes = 0;
    private Bot bot;
    private boolean isSinglePlayer = false;
    private boolean isPlaying;
    private SoundManager soundManager;
    private MediaPlayer mediaPlayer;
    Dialog dailog;
    ImageView btnresume, btnmainmenu, btnsound, btnexit;
    private boolean isUserTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // sound
        soundManager = new SoundManager(this, R.raw.laser);
        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
        mediaPlayer.setLooping(true); // Set to loop indefinitely
        mediaPlayer.setVolume(1.0f, 1.0f);
        isPlaying = true;
        mediaPlayer.start();

        bot = new Bot(); // Initialize the bot
        isSinglePlayer = getIntent().getBooleanExtra("singlePlayer", false);

        // Dailog
        dailog = new Dialog(this);
        dailog.setContentView(R.layout.dialog_pause_game_menu);
        Objects.requireNonNull(dailog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnresume = dailog.getWindow().findViewById(R.id.resume);
        btnmainmenu = dailog.getWindow().findViewById(R.id.mainmenu);
        btnsound = dailog.getWindow().findViewById(R.id.sound);
        btnexit = dailog.getWindow().findViewById(R.id.exit);

        btnresume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    soundManager.playSound();
                }
                dailog.dismiss();
            }
        });
        btnmainmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    soundManager.playSound();
                }
                Intent intent = new Intent(MainActivity.this, PlayerChoice.class);
                startActivity(intent);
                finish();
            }
        });
        btnsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    // Stop the music if it is currently playing
                    mediaPlayer.pause();
                    soundManager.stopSound();
                    btnsound.setImageResource(R.drawable.soundoff);
                    isPlaying =false;
                } else {
                    // Start the music if it is currently not playing
                    mediaPlayer.start();
                    soundManager.playSound();
                    btnsound.setImageResource(R.drawable.soundon);
                    isPlaying = true;
                }
            }
        });


        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    soundManager.playSound();
                }
                Toast.makeText(MainActivity.this, "Game Closed", Toast.LENGTH_SHORT).show();
            }
        });





        binding.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    soundManager.playSound();
                }
                dailog.show();
            }
        });

        combinationList.add(new int[] {0,1,2});
        combinationList.add(new int[] {3,4,5});
        combinationList.add(new int[] {6,7,8});
        combinationList.add(new int[] {0,3,6});
        combinationList.add(new int[] {1,4,7});
        combinationList.add(new int[] {2,5,8});
        combinationList.add(new int[] {2,4,6});
        combinationList.add(new int[] {0,4,8});

        String getPlayerOneName = getIntent().getStringExtra("playerOne");
        String getPlayerTwoName = getIntent().getStringExtra("playerTwo");
        binding.playerOneName.setText(getPlayerOneName);
        binding.playerTwoName.setText(getPlayerTwoName);

        binding.image1.setOnClickListener(createClickListener(0));
        binding.image2.setOnClickListener(createClickListener(1));
        binding.image3.setOnClickListener(createClickListener(2));
        binding.image4.setOnClickListener(createClickListener(3));
        binding.image5.setOnClickListener(createClickListener(4));
        binding.image6.setOnClickListener(createClickListener(5));
        binding.image7.setOnClickListener(createClickListener(6));
        binding.image8.setOnClickListener(createClickListener(7));
        binding.image9.setOnClickListener(createClickListener(8));
    }

    private View.OnClickListener createClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUserTurn && isBoxSelectable(position)) {
                    performAction((ImageView) view, position);
                }
            }
        };
    }

    private void performAction(ImageView imageView, int selectedBoxPosition) {
        boxPositions[selectedBoxPosition] = playerTurn;
        if (playerTurn == 1) {
            imageView.setImageResource(R.drawable.x_image);
            // Play the sound effect
            if (isPlaying) {
                soundManager.playSound();
            } else {
                soundManager.stopSound();
            }
            if (checkResults()) {
                showResult(binding.playerOneName.getText().toString());
            } else if (totalSelectedBoxes == 9) {
                showResult("Match Draw");
            } else {
                changePlayerTurn(2);
                totalSelectedBoxes++;
                if (isSinglePlayer) {
                    isUserTurn = false;
                    botMove();
                }
            }
        } else {
            imageView.setImageResource(R.drawable.o_image);
            // Play the sound effect
            if (isPlaying) {
                soundManager.playSound();
            } else {
                soundManager.stopSound();
            }
            if (checkResults()) {
                showResult(binding.playerTwoName.getText().toString());
            } else if (totalSelectedBoxes == 9) {
                showResult("Match Draw");
            } else {
                changePlayerTurn(1);
                totalSelectedBoxes++;
            }
        }
    }

    private void showResult(String winner) {
        ResultDialog resultDialog = new ResultDialog(MainActivity.this, winner, MainActivity.this);
        resultDialog.setCancelable(false);
        resultDialog.show();
    }

    private void changePlayerTurn(int currentPlayerTurn) {
        playerTurn = currentPlayerTurn;
        if (playerTurn == 1) {
            binding.playerX.setBackgroundResource(R.drawable.black_border);
            binding.playerO.setBackgroundResource(R.drawable.white_box);
        } else {
            binding.playerX.setBackgroundResource(R.drawable.white_box);
            binding.playerO.setBackgroundResource(R.drawable.black_border);
        }
    }

    private boolean checkResults() {
        for (int[] combination : combinationList) {
            if (boxPositions[combination[0]] == playerTurn &&
                    boxPositions[combination[1]] == playerTurn &&
                    boxPositions[combination[2]] == playerTurn) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoxSelectable(int boxPosition) {
        return boxPositions[boxPosition] == 0;
    }
    // Bot intergration
    private void botMove() {
        // Check if there are any moves left for the bot
        boolean movesLeft = false;
        for (int position : boxPositions) {
            if (position == 0) {
                movesLeft = true;
                break;
            }
        }
        // If no moves are left, check for a result and return
        if (!movesLeft) {
            showResult("Match Draw");
            return; // Exit the method to prevent further execution
        }
        // Delay the bot's move by 1 second (1000 milliseconds)
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int move = bot.getMove(boxPositions);
                ImageView botImageView = getImageViewForPosition(move);
                performAction(botImageView, move);
                isUserTurn = true;
            }
        }, 1000); // 1000 milliseconds delay
    }
    private ImageView getImageViewForPosition(int position) {
        switch (position) {
            case 0: return binding.image1;
            case 1: return binding.image2;
            case 2: return binding.image3;
            case 3: return binding.image4;
            case 4: return binding.image5;
            case 5: return binding.image6;
            case 6: return binding.image7;
            case 7: return binding.image8;
            case 8: return binding.image9;
            default: return null;
        }
    }

    public void restartMatch() {
        boxPositions = new int[] {0,0,0,0,0,0,0,0,0}; //9 zero
        playerTurn = 1;
        totalSelectedBoxes = 0;
        binding.image1.setImageResource(R.drawable.white_box);
        binding.image2.setImageResource(R.drawable.white_box);
        binding.image3.setImageResource(R.drawable.white_box);
        binding.image4.setImageResource(R.drawable.white_box);
        binding.image5.setImageResource(R.drawable.white_box);
        binding.image6.setImageResource(R.drawable.white_box);
        binding.image7.setImageResource(R.drawable.white_box);
        binding.image8.setImageResource(R.drawable.white_box);
        binding.image9.setImageResource(R.drawable.white_box);
    }
}
