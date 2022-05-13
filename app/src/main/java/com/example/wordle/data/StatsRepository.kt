package com.example.wordle.data

import android.content.Context

/**
 * Class responsible for communicating with the [stats] SharedPreferences
 */
class StatsRepository(context: Context) {

    private val stats = SharedPreferences(context).stats

    fun getPlayCount(): Int = stats.getInt("play_count", 0)

    fun getWinCount(): Int = stats.getInt("win_count", 0)

    fun getCurrentStreak(): Int = stats.getInt("current_streak", 0)

    fun getMaxStreak(): Int = stats.getInt("max_streak", 0)

    fun getWins(row: Int): Int = stats.getInt("wins_$row", 0)

    fun setPlayCount() {
        val playCount = stats.getInt("play_count", 0).plus(1)
        stats.edit().putInt("play_count", playCount).apply()
    }

    fun updateStats(winner: Boolean, row: Int) {
        with(stats.edit()) {
            if (winner) {
                val winRow = "wins_$row"
                putInt(winRow, stats.getInt(winRow, 0).plus(1))
                putInt("win_count", stats.getInt("win_count", 0).plus(1))
                putInt("current_streak", stats.getInt("current_streak", 0).plus(1))
                apply()
                if (stats.getInt("current_streak", 0) > stats.getInt("max_streak", 0)) {
                    putInt("max_streak", stats.getInt("current_streak", 0))
                }
            } else {
                putInt("current_streak", 0)
            }
            apply()
        }
    }

    fun resetStats() {
        stats.edit().clear().apply()
    }

}