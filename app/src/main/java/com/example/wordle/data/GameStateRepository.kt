package com.example.wordle.data

import android.content.Context
import android.util.Log
import com.example.wordle.ui.model.ViewState

/**
 * Class responsible for communicating with [gameState] SharedPreferences
 * and parsing data as needed
 */
class GameStateRepository(context: Context) {

    private val gameState = SharedPreferences(context).gameState

    fun getAnswer(): String = gameState.getString("answer", "")!!

    fun getTries(): Int = gameState.getInt("tries", 0)

    fun getCurrentWord(): String = gameState.getString("current_word", "")!!

    fun getTransition(): String? = gameState.getString("transition", null)

    fun getLetters(): List<Char> = gameState.getString("letters", "")!!.toList()

    fun getGameInProgress(): Boolean = gameState.getBoolean("game_in_progress", false)

    fun getTileStates(): List<ViewState> {
        val savedTileStates = gameState.getString("tile_states", "")!!.split(", ")
        val tileStates: MutableList<ViewState> = mutableListOf()
        if (savedTileStates != listOf("")) {
            for (item in savedTileStates) {
                tileStates.add(item.toViewState()!!)
            }
        }
        return tileStates.toList()
    }

    fun getKeyStates(): Map<Char, ViewState?> {
        val savedKeyStates = gameState.getStringSet("key_states", setOf())
        val keyStates: MutableMap<Char, ViewState?> = mutableMapOf()
        for (item in savedKeyStates!!) {
            keyStates[item[0]] = item.drop(2).toViewState()
        }
        return keyStates.toMap()
    }

    fun newGame(answer: String) {
        with(gameState.edit()) {
            clear().apply()
            putString("answer", answer)
            putBoolean("game_in_progress", true)
            apply()
        }
    }

    fun updateGameState(
        tries: Int,
        letters: List<Char>?,
        currentWord: String?,
        tileStates: List<ViewState>?,
        keyStates: Map<Char, ViewState?>,
        transition: String?
    ) {
        val keyStatesSet: MutableSet<String> = mutableSetOf()
        for (item in keyStates) {
            keyStatesSet += item.toString()
        }
        with(gameState.edit()) {
            putInt("tries", tries)
            putString("letters", letters?.joinToString(""))
            putString("current_word", currentWord)
            putString("tile_states", tileStates?.joinToString())
            putStringSet("key_states", keyStatesSet)
            putString("transition", transition)
            apply()
        }
    }

    fun setGameInProgress(gameInProgress: Boolean) {
        gameState.edit().putBoolean("game_in_progress", gameInProgress).apply()
    }

    fun resetGameState() {
        gameState.edit().clear().apply()
    }

    /**
     * Converts Strings to [ViewState]s
     */
    private fun String.toViewState(): ViewState? {
        return when (this) {
            "FILLED" -> ViewState.FILLED
            "CORRECT" -> ViewState.CORRECT
            "PRESENT" -> ViewState.PRESENT
            "ABSENT" -> ViewState.ABSENT
            else -> null
        }
    }
}

