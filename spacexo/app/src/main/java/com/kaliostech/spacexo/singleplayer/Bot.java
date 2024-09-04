package com.kaliostech.spacexo.singleplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot {
    private final Random random = new Random();
    private final int PLAYER_X = 1;
    private final int PLAYER_O = 2;

    public int getMove(int[] boxPositions) {
        // Define a threshold to decide when to make a random move
        double randomnessFactor = 0.2; // 20% of the time, the bot will make a random move

        if (random.nextDouble() < randomnessFactor) {
            // Make a random move
            List<Integer> availablePositions = new ArrayList<>();
            for (int i = 0; i < boxPositions.length; i++) {
                if (boxPositions[i] == 0) {
                    availablePositions.add(i);
                }
            }
            return availablePositions.get(random.nextInt(availablePositions.size()));
        }

        // Otherwise, use the Minimax algorithm to make the best move
        int bestMove = -1;
        int bestValue = Integer.MIN_VALUE;

        for (int i = 0; i < boxPositions.length; i++) {
            if (boxPositions[i] == 0) {
                // Try the move
                boxPositions[i] = PLAYER_O;
                int moveValue = minimax(boxPositions, 0, false);
                boxPositions[i] = 0; // Undo the move

                if (moveValue > bestValue) {
                    bestMove = i;
                    bestValue = moveValue;
                }
            }
        }

        return bestMove;
    }

    private int minimax(int[] boxPositions, int depth, boolean isMaximizing) {
        int score = evaluate(boxPositions);

        // If the game is over, return the score
        if (score == 10) return score - depth;
        if (score == -10) return score + depth;
        if (isBoardFull(boxPositions)) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;

            for (int i = 0; i < boxPositions.length; i++) {
                if (boxPositions[i] == 0) {
                    boxPositions[i] = PLAYER_O;
                    best = Math.max(best, minimax(boxPositions, depth + 1, false));
                    boxPositions[i] = 0;
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;

            for (int i = 0; i < boxPositions.length; i++) {
                if (boxPositions[i] == 0) {
                    boxPositions[i] = PLAYER_X;
                    best = Math.min(best, minimax(boxPositions, depth + 1, true));
                    boxPositions[i] = 0;
                }
            }
            return best;
        }
    }

    private int evaluate(int[] boxPositions) {
        // Check rows for victory
        for (int row = 0; row < 3; row++) {
            if (boxPositions[row * 3] == boxPositions[row * 3 + 1] &&
                    boxPositions[row * 3 + 1] == boxPositions[row * 3 + 2]) {
                if (boxPositions[row * 3] == PLAYER_O) return 10;
                if (boxPositions[row * 3] == PLAYER_X) return -10;
            }
        }

        // Check columns for victory
        for (int col = 0; col < 3; col++) {
            if (boxPositions[col] == boxPositions[col + 3] &&
                    boxPositions[col + 3] == boxPositions[col + 6]) {
                if (boxPositions[col] == PLAYER_O) return 10;
                if (boxPositions[col] == PLAYER_X) return -10;
            }
        }

        // Check diagonals for victory
        if (boxPositions[0] == boxPositions[4] && boxPositions[4] == boxPositions[8]) {
            if (boxPositions[0] == PLAYER_O) return 10;
            if (boxPositions[0] == PLAYER_X) return -10;
        }
        if (boxPositions[2] == boxPositions[4] && boxPositions[4] == boxPositions[6]) {
            if (boxPositions[2] == PLAYER_O) return 10;
            if (boxPositions[2] == PLAYER_X) return -10;
        }

        // If no one has won yet
        return 0;
    }

    private boolean isBoardFull(int[] boxPositions) {
        for (int pos : boxPositions) {
            if (pos == 0) return false;
        }
        return true;
    }
}
