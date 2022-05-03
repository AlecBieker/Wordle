package com.example.wordle.data


import android.content.Context
import android.content.SharedPreferences

data class SharedPrefs(val context: Context) {

    /**
     * [SharedPreferences] handle for saved game state
     */
    val gameState: SharedPreferences = context.getSharedPreferences(
        "game_state",
        Context.MODE_PRIVATE
    )

    /**
     * [SharedPreferences] handle for game stats
     */
    val stats: SharedPreferences = context.getSharedPreferences(
        "stats",
        Context.MODE_PRIVATE
    )

    /**
     * [SharedPreferences] handle for user settings
     */
    val settings: SharedPreferences = context.getSharedPreferences(
        "settings",
        Context.MODE_PRIVATE
    )
}
