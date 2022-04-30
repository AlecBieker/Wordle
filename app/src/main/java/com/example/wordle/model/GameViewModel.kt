package com.example.wordle.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.wordle.data.wordsList1
import com.example.wordle.data.wordsList2
import com.example.wordle.worker.NotificationWorker
import org.jetbrains.annotations.NotNull
import java.util.concurrent.TimeUnit
import kotlin.random.Random

enum class ViewState { FILLED, CORRECT, PRESENT, ABSENT }

/**
 * [GameViewModel] holds all the variables representing the apps current state as well as
 * all the logic controlling how those variables are modified based on user input.
 */

class GameViewModel(app: Application) : AndroidViewModel(app) {

    // shared preferences handle for saved game state
    val gameState: SharedPreferences = getApplication<Application>().getSharedPreferences(
        "game_state",
        Context.MODE_PRIVATE
    )

    // shared preferences handle for game stats
    val stats: SharedPreferences = getApplication<Application>().getSharedPreferences(
        "stats",
        Context.MODE_PRIVATE
    )

    // shared preferences handle for user settings
    val settings: SharedPreferences = getApplication<Application>().getSharedPreferences(
        "settings",
        Context.MODE_PRIVATE
    )

    private val workManager = WorkManager.getInstance(app)

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // values of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<Char>>()
    val letters: LiveData<List<Char>> = _letters

    // the states of the game tiles as represented by enum class ViewState
    private val _tileStates = MutableLiveData<List<ViewState?>>()
    val tilesStates: LiveData<List<ViewState?>> = _tileStates

    // the states of the keyboard keys
    private val _keyStates = MutableLiveData<Map<Char, ViewState?>>()
    val keyStates: LiveData<Map<Char, ViewState?>> = _keyStates

    // Last word to be guessed
    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    // Number of words the user has guessed out of 6
    private val _tries = MutableLiveData<Int>()
    val tries: LiveData<Int> = _tries

    // GameFragment's current transition
    private val _transition = MutableLiveData<String?>()
    val transition: LiveData<String?> = _transition

    /**
     * Sets the theme for the app
     */
    fun setTheme(theme: Int) {
        Log.d("ViewModel", "setTheme() called")
        settings.edit().putInt("theme", theme).apply()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    /**
     * Turns hard mode on or off
     */
    fun setHardMode(hardMode: Boolean) {
        Log.d("ViewModel", "toggleHardMode($hardMode) called")
        settings.edit().putBoolean("hard_mode", hardMode).apply()
    }

    /**
     * Updates the [_transition] live data variable
     */
    fun updateTransition(transition: String?) {
        Log.d("ViewModel", "updateTransition() called")
        _transition.value = transition
    }

    /**
     * Turns Notifications on and off
     */
    fun setNotifications(isChecked: Boolean) {
        Log.d("ViewModel", "setNotifications() called")
        settings.edit().putBoolean("notifications", isChecked).apply()
        if (!isChecked) {
            cancelNotifications()
        }
    }

    /**
     * Ques a periodicWorkRequest with [NotificationWorker]
     */
    fun queNotifications() {
        Log.d("ViewModel", "queNotifications() called")
        if (settings.getBoolean("notifications", true)) {
            val workRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                24,
                TimeUnit.HOURS
            )
                .setInitialDelay(24, TimeUnit.HOURS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "notifications",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    /**
     * Cancels the notifications periodicWorkRequest
     */
    fun cancelNotifications() {
        Log.d("ViewModel", "cancelNotifications() called")
        workManager.cancelUniqueWork("notifications")
    }

    /**
     * Resets user statistics
     */
    fun resetStats() {
        Log.d("ViewModel", "resetStats() called")
        stats.edit().clear().apply()
        gameState.edit().clear().apply()
    }

    /**
     * Initiates a new game
     */
    fun newGame() {
        Log.d("ViewModel", "newGame() called")
        resetVariables()
        _answer.value = generate()
        _transition.value = null
        with(gameState.edit()) {
            clear().apply()
            putString("answer", _answer.value)
            putBoolean("game_in_progress", true)
            apply()
        }
        stats.edit().putInt("play_count", stats.getInt("play_count", 0).plus(1)).apply()
        Log.d("ViewModel", "answer = ${answer.value}")
    }

    /**
     * Resets all game parameters to the default values
     */
    private fun resetVariables() {
        Log.d("ViewModel", "resetVariables() called")
        _currentWord.value = ""
        _tries.value = 0
        _letters.value = listOf()
        _tileStates.value = listOf()
        _keyStates.value = mapOf()
        _transition.value = null
    }

    /**
     * Generates a new word at random from [wordsList1] and [wordsList2]
     */
    private fun generate(): String {
        Log.d("ViewModel", "generate() called")
        val rand = Random(System.nanoTime())
        return (wordsList1 + wordsList2).random(rand)
    }

    /**
     * Sets all LiveData variables using [gameState] shared preferences file
     */
    fun resumeGame() {
        resetVariables()
        _answer.value = gameState.getString("answer", "")
        _tries.value = gameState.getInt("tries", 0)
        _currentWord.value = gameState.getString("current_word", "")
        _transition.value = gameState.getString("transition", null)
        val savedLetters = gameState.getString("letters", "")!!.toList()
        val savedTileStates = gameState.getString("tile_states", "")!!.split(", ")
        val savedKeyStates = gameState.getStringSet("key_states", setOf())
        for (item in savedLetters) {
            _letters.value = _letters.value?.plus(item)
        }
        for (item in savedTileStates) {
            _tileStates.value = _tileStates.value?.plus(item.toViewState())
        }
        val map: MutableMap<Char, ViewState?> = mutableMapOf()
        for (item in savedKeyStates!!) {
            map[item[0]] = item.drop(2).toViewState()
        }
        _keyStates.value = map.toMap()
    }


    /**
     * Updates the [gameState] sharedPreferences file
     */
    fun updateGameState() {
        Log.d("ViewModel", "updateGameState() called")
        val keyStatesSet: MutableSet<String> = mutableSetOf()
        for (item in _keyStates.value!!) {
            keyStatesSet += item.toString()
        }
        with(gameState.edit()) {
            putInt("tries", _tries.value!!)
            putString("letters", _letters.value?.joinToString(separator = ""))
            putString("current_word", _currentWord.value)
            putString("tile_states", _tileStates.value?.joinToString())
            putStringSet("key_states", keyStatesSet)
            putString("transition", _transition.value)
            apply()
        }
    }

    /**
     * Retrieves the win percentage
     */
    fun getWinPercentage(plays: Int): String {
        val wins = stats.getInt("win_count", 0).toDouble()
        return "%.1f".format(wins.div(plays.toDouble()).times(100))
    }

    /**
     * Ends the game and updates stats
     */
    fun updateStats(winner: Boolean) {
        gameState.edit().putBoolean("game_in_progress", false).apply()
        with(stats.edit()) {
            if (winner) {
                val winRow = "wins_${_tries.value?.plus(1)}"
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

    /**
     * Updates the [_letters] and [_currentWord] values
     */
    fun updateText(char: Char) {
        Log.d("ViewModel", "setText($char) called")
        val size = _currentWord.value!!.length
        if (char == ' ' && size != 0) {
            _letters.value = _letters.value?.dropLast(1)
            _tileStates.value = _tileStates.value?.dropLast(1)
            _currentWord.value = _currentWord.value?.dropLast(1)
        } else if (char != ' ' && size != 5) {
            _letters.value = _letters.value?.plus(char)
            _tileStates.value = _tileStates.value?.plus(ViewState.FILLED)
            _currentWord.value += char
        }
    }

    /**
     * Clears the contents of the current row
     */
    fun clearRow(): Boolean {
        Log.d("ViewModel", "clearRow() called")
        val size = _currentWord.value!!.length
        _letters.value = _letters.value?.dropLast(size)
        _tileStates.value = _tileStates.value?.dropLast(size)
        _currentWord.value = ""
        return true
    }

    /**
     * Updates the [_tileStates] live data to show hints
     */
    fun updateTileStates(ind: Int, hint: ViewState) {
        val i: Int = _tileStates.value!!.size.minus(5).plus(ind)
        val mutableStates = _tileStates.value!!.toMutableList()
        mutableStates[i] = hint
        _tileStates.value = mutableStates.toList()
    }

    /**
     * Updates the [_keyStates] live data
     */
    fun updateKeyStates(hints: List<ViewState>) {
        val map = _keyStates.value!!.toMutableMap()
        for (i in 0..4) {
            val char = _currentWord.value!![i]
            if ((hints[i] != ViewState.CORRECT) &&
                (map[char] == ViewState.CORRECT || map[char] == ViewState.PRESENT)
            ) {
                continue
            } else {
                map[char] = hints[i]
            }
        }
        _keyStates.value = map.toMap()
    }

    /**
     * Checks if the entered word uses the hints from the previous guess
     */
    fun isUsingHints(): Triple<Int, Int, Char> {
        Log.d("ViewModel", "isUsingHints() called")
        val word = _currentWord.value!!
        val index = _tileStates.value!!.size.minus(10)
        for (i in 0..4) {
            val letter = _letters.value!![index + i]
            when (_tileStates.value!![index + i]) {
                ViewState.ABSENT -> continue
                ViewState.PRESENT -> {
                    if (letter in word) {
                        continue
                    } else {
                        return Triple(1, 0, letter)
                    }
                }
                ViewState.CORRECT -> {
                    if (letter == word[i]) {
                        continue
                    } else {
                        return Triple(2, i, letter)
                    }
                }
                else -> {}
            }
        }
        return Triple(0, 0, ' ')
    }

    /**
     * Checks if the word is a valid word from the lists
     */
    fun isAWord(): Int {
        Log.d("ViewModel", "isAWord() called")
        return when {
            currentWord.value!!.length != 5 -> 0
            currentWord.value in wordsList1 || currentWord.value in wordsList2 -> 1
            else -> 2
        }
    }

    /**
     * Checks each letter for whether it is correct, present or absent
     * with respect to the amount of the letter in the answer
     * and creates a list of [ViewState]s representing each state.
     */
    fun isLetterCorrect(): List<ViewState> {
        Log.d("ViewModel", "isLetterCorrect() called")
        val hints = mutableListOf<ViewState?>(null, null, null, null, null)
        val tempWord = _answer.value!!.toMutableList()
        // set CORRECT ViewStates
        for (i in 0..4) {
            if (_currentWord.value!![i] == tempWord[i]) {
                hints[i] = ViewState.CORRECT
                tempWord[i] = '_'
            }
        }
        // set PRESENT and ABSENT ViewStates
        for (i in 0..4) {
            when {
                hints[i] == ViewState.CORRECT -> continue
                _currentWord.value!![i] in tempWord -> {
                    hints[i] = ViewState.PRESENT
                    tempWord[tempWord.indexOf(_currentWord.value!![i])] = '_'
                }
                else -> hints[i] = ViewState.ABSENT
            }
        }
        return hints.filterNotNull()
    }

    /**
     * Checks if the input word is correct or not and returns an Int
     */
    fun isWordCorrect(): Int {
        Log.d("ViewModel", "isCorrect() called")
        return when {
            _currentWord.value == _answer.value -> 0
            _tries.value!! >= 5 -> 1
            else -> {
                nextRow()
                2
            }
        }
    }

    /**
     * Moves down a row, resets [_currentWord] and saves progress to [gameState]
     */
    private fun nextRow() {
        Log.d("ViewModel", "nextRow() called")
        _currentWord.value = ""
        _tries.value = _tries.value?.plus(1)
        updateGameState()
    }
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
