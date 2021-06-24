package com.knowledgespike.sudokusolver

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

class Solution(val board: Array<CharArray>) {
    suspend fun solve(animate: Boolean = true) = flow {
        backtrack(board, 0, 0, animate) { ndx, value ->
            emit(Pair(ndx, value))
        }
    }

    private suspend fun backtrack(
        board: Array<CharArray>,
        row: Int,
        col: Int,
        animate: Boolean = true,
        onChange: suspend (Int, Char) -> Unit
    ): Boolean {
        val INITIAL_DELAY_COUNT = 4
        var delayCount = INITIAL_DELAY_COUNT
        //traverse from left top to bottom right
        if (col == 9) { //checked all row elements, continue with the next one.
            return backtrack(board, row + 1, 0, animate, onChange)
        }
        if (row == 9) {
            return true //we reached the bottom right and found solution.
        }
        if (board[row][col] != '.') { //already set, check next element.
            return backtrack(board, row, col + 1, animate, onChange)
        }
        var c = '1'
        while (c <= '9') {
            if (!isValid(board, row, col, c)) {
                c++
                continue
            }
            board[row][col] = c
            if (delayCount-- == 0 && animate) {
                delay(1)
                delayCount = INITIAL_DELAY_COUNT
            }
            onChange((row * 9) + col, c)
            if (backtrack(board, row, col + 1, animate, onChange)) {
                return true
            }
            board[row][col] = '.'
            if (delayCount-- == 0 && animate) {
                delay(1)
                delayCount = INITIAL_DELAY_COUNT
            }
            onChange((row * 9) + col, '.')
            c++
        }
        return false
    }


    private fun isValid(board: Array<CharArray>, row: Int, col: Int, c: Char): Boolean {
        for (i in 0..8) {
            if (board[row][i] == c || board[i][col] == c || board[row / 3 * 3 + i / 3][col / 3 * 3 + i % 3] == c) {
                return false
            }
        }
        return true
    }
}