package com.kaliostech.spacexo.ai

import kotlin.random.Random

class Bot {
    private val random = Random.Default
    private val PLAYER_X = 1
    private val PLAYER_O = 2

    fun getMove(boxPositions: IntArray): Int {
        val randomnessFactor = 0.2 // 20% of the time, the bot will make a random move

        if (random.nextDouble() < randomnessFactor) {
            // Make a random move
            val availablePositions = boxPositions.indices.filter { boxPositions[it] == 0 }
            return availablePositions[random.nextInt(availablePositions.size)]
        }

        // Otherwise, use the Minimax algorithm to make the best move
        var bestMove = -1
        var bestValue = Int.MIN_VALUE

        for (i in boxPositions.indices) {
            if (boxPositions[i] == 0) {
                // Try the move
                boxPositions[i] = PLAYER_O
                val moveValue = minimax(boxPositions, 0, false)
                boxPositions[i] = 0 // Undo the move

                if (moveValue > bestValue) {
                    bestMove = i
                    bestValue = moveValue
                }
            }
        }

        return bestMove
    }

    private fun minimax(boxPositions: IntArray, depth: Int, isMaximizing: Boolean): Int {
        val score = evaluate(boxPositions)

        // If the game is over, return the score
        when {
            score == 10 -> return score - depth
            score == -10 -> return score + depth
            isBoardFull(boxPositions) -> return 0
        }

        if (isMaximizing) {
            var best = Int.MIN_VALUE

            for (i in boxPositions.indices) {
                if (boxPositions[i] == 0) {
                    boxPositions[i] = PLAYER_O
                    best = maxOf(best, minimax(boxPositions, depth + 1, false))
                    boxPositions[i] = 0
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE

            for (i in boxPositions.indices) {
                if (boxPositions[i] == 0) {
                    boxPositions[i] = PLAYER_X
                    best = minOf(best, minimax(boxPositions, depth + 1, true))
                    boxPositions[i] = 0
                }
            }
            return best
        }
    }

    private fun evaluate(boxPositions: IntArray): Int {
        // Check rows for victory
        for (row in 0..2) {
            if (boxPositions[row * 3] == boxPositions[row * 3 + 1] &&
                boxPositions[row * 3 + 1] == boxPositions[row * 3 + 2]
            ) {
                when (boxPositions[row * 3]) {
                    PLAYER_O -> return 10
                    PLAYER_X -> return -10
                }
            }
        }

        // Check columns for victory
        for (col in 0..2) {
            if (boxPositions[col] == boxPositions[col + 3] &&
                boxPositions[col + 3] == boxPositions[col + 6]
            ) {
                when (boxPositions[col]) {
                    PLAYER_O -> return 10
                    PLAYER_X -> return -10
                }
            }
        }

        // Check diagonals for victory
        if (boxPositions[0] == boxPositions[4] && boxPositions[4] == boxPositions[8]) {
            when (boxPositions[0]) {
                PLAYER_O -> return 10
                PLAYER_X -> return -10
            }
        }
        if (boxPositions[2] == boxPositions[4] && boxPositions[4] == boxPositions[6]) {
            when (boxPositions[2]) {
                PLAYER_O -> return 10
                PLAYER_X -> return -10
            }
        }

        // If no one has won yet
        return 0
    }

    private fun isBoardFull(boxPositions: IntArray): Boolean {
        return boxPositions.none { it == 0 }
    }
}