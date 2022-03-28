package com.example.wordle.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wordle.R
import com.example.wordle.data.wordsList1
import com.example.wordle.data.wordsList2

/**
 * [GameViewModel] holds all the variables for reinstating a saved game as well as all the logic
 * deciding what will be shown on the screen as the game is played
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

    val settings: SharedPreferences = getApplication<Application>().getSharedPreferences(
        "settings",
        Context.MODE_PRIVATE
    )

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // values of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<Char>>()
    val letters: LiveData<List<Char>> = _letters

    // colors or drawable resources of the textView Backgrounds
    private val _backgrounds = MutableLiveData<List<Int>>()
    val backgrounds: LiveData<List<Int>> = _backgrounds

    // colors of the textViews' text
    private val _textColors = MutableLiveData<List<Int>>()
    val textColors: LiveData<List<Int>> = _textColors

    // colors of the keys
    private val _keyColors = MutableLiveData<Map<Char, Int>>()
    val keyColors: LiveData<Map<Char, Int>> = _keyColors

    // colors of the keys' text
    private val _keyTextColors = MutableLiveData<Map<Char, Int>>()
    val keyTextColors: LiveData<Map<Char, Int>> = _keyTextColors

    // border drawable for textViews that don't have a color set yet
    val border = R.drawable.border

    // default color value for keys that don't have a color set yet
    val keyColor = R.color.key_color

    // default color value for text that doesn't have a color set yet
    val textColor = R.color.text_color

    // Last word to be guessed
    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    // Number of words the user has guessed out of 6
    private val _tries = MutableLiveData<Int>()
    val tries: LiveData<Int> = _tries

    // GameFragment's current transition
    private val _transition = MutableLiveData<String?>()
    val transition: LiveData<String?> = _transition

    fun resetStats() {
        Log.d("ViewModel", "resetStats() called")
        stats.edit().clear().apply()
        gameState.edit().clear().apply()
    }

    fun setTheme(theme: Int) {
        Log.d("ViewModel", "setTheme() called")
        settings.edit().putInt("theme", theme).apply()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    fun setHardMode(hardMode: Boolean) {
        Log.d("ViewModel", "toggleHardMode($hardMode) called")
        with(settings.edit()) {
            putBoolean("hard_mode", hardMode)
            apply()
        }
    }

    fun updateTransition (transition: String?) {
        Log.d("ViewModel", "updateTransition() called")
        _transition.value = transition
    }

    // initiates a new game
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
    }

    // resets all game parameters to the default values
    private fun resetVariables() {
        Log.d("ViewModel", "resetVariables() called")
        _currentWord.value = ""
        _tries.value = 0
        _letters.value = listOf()
        _backgrounds.value = listOf()
        _textColors.value = listOf()
        _keyColors.value = mapOf()
        _keyTextColors.value = mapOf()
        _transition.value = null
    }

    // generates a new word at random from wordsList
    private fun generate(): String {
        Log.d("ViewModel", "generate() called")
        return (wordsList1 + wordsList2).random()
    }

    // sets all LiveData variables using gameState shared preferences file
    fun resumeGame() {
        resetVariables()
        _answer.value = gameState.getString("answer", "")
        _tries.value = gameState.getInt("tries", 0)
        _currentWord.value = gameState.getString("current_word", "")
        _transition.value = gameState.getString("transition", null)
        val savedLetters = gameState.getString("letters", "")!!.toList()
        val savedBackgrounds = gameState.getString("backgrounds", "")!!.split(", ")
        val savedTextColors = gameState.getString("text_colors", "")!!.split(", ")
        val savedKeyColors = gameState.getStringSet("key_colors", setOf(""))
        for (item in savedLetters) {
            _letters.value = _letters.value?.plus(item)
        }
        if (savedBackgrounds != listOf("")) {
            for (item in savedBackgrounds) {
                _backgrounds.value = _backgrounds.value?.plus(item.toInt())
            }
        }
        if (savedTextColors != listOf("")) {
            for (item in savedTextColors) {
                _textColors.value = _textColors.value?.plus(item.toInt())
            }
        }
        val map: MutableMap<Char, Int> = mutableMapOf()
        val textMap: MutableMap<Char, Int> = mutableMapOf()
        if (!savedKeyColors.isNullOrEmpty()) {
            for (item in savedKeyColors) {
                map[item[0]] = item.drop(2).toInt()
                textMap[item[0]] = R.color.hint_text_color
            }
        }
        _keyColors.value = map.toMap()
        _keyTextColors.value = textMap.toMap()
    }


    // updates the "game_state" sharedPreferences file
    fun updateGameState() {
        Log.d("ViewModel", "updateGameState() called")
        val keyColorsSet: MutableSet<String> = mutableSetOf()
        val keyTextColorsSet: MutableSet<String> = mutableSetOf()
        for (item in _keyColors.value!!) keyColorsSet += item.toString()
        for (item in _keyTextColors.value!!) keyTextColorsSet += item.toString()
        with(gameState.edit()) {
            putInt("tries", _tries.value!!)
            putString("letters", _letters.value?.joinToString(separator = ""))
            putString("current_word", _currentWord.value)
            putString("backgrounds", _backgrounds.value?.joinToString())
            putString("text_colors", _textColors.value?.joinToString())
            putStringSet("key_colors", keyColorsSet)
            putString("transition", _transition.value)
            apply()
        }
    }

    // retrieves the win percentage
    fun getWinPercentage(plays: Int): String {
        val wins = stats.getInt("win_count", 0).toDouble()
        return "%.1f".format(wins.div(plays.toDouble()).times(100))
    }

    // ends the game and updates stats
    fun updateStats(winner: Boolean) {
        gameState.edit().putBoolean("game_in_progress", false).apply()
        with(stats.edit()) {
            if (winner) {
                when (_tries.value) {
                    0 -> putInt("wins_1", stats.getInt("wins_1", 0).plus(1))
                    1 -> putInt("wins_2", stats.getInt("wins_2", 0).plus(1))
                    2 -> putInt("wins_3", stats.getInt("wins_3", 0).plus(1))
                    3 -> putInt("wins_4", stats.getInt("wins_4", 0).plus(1))
                    4 -> putInt("wins_5", stats.getInt("wins_5", 0).plus(1))
                    5 -> putInt("wins_6", stats.getInt("wins_6", 0).plus(1))
                }
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

    // updates the letters and currentWord val
    fun updateText(char: Char) {
        Log.d("ViewModel", "setText($char) called")
        val size = _currentWord.value!!.length
        if (char == ' ' && size != 0) {
            _letters.value = _letters.value?.dropLast(1)
            _backgrounds.value = _backgrounds.value?.dropLast(1)
            _currentWord.value = _currentWord.value?.dropLast(1)
        } else if (char != ' ' && size != 5) {
            _letters.value = _letters.value?.plus(char)
            _backgrounds.value = _backgrounds.value?.plus(R.drawable.filled_border)
            _currentWord.value += char
        }
    }

    // Clears the contents of the current row
    fun clearRow(): Boolean {
        Log.d("ViewModel", "clearRow() called")
        val size = _currentWord.value!!.length
        _letters.value = _letters.value?.dropLast(size)
        _currentWord.value = ""
        return true
    }

    // updates the colors liveData val to
    fun updateBackgrounds(ind: Int, hint: Int) {
        val i: Int = _backgrounds.value!!.size.minus(5).plus(ind)
        val mutableBackgrounds = _backgrounds.value!!.toMutableList()
        mutableBackgrounds[i] = hint
        _backgrounds.value = mutableBackgrounds.toList()
        _textColors.value = _textColors.value!!.plus(R.color.hint_text_color)
    }

    // updates the key background and text colors
    fun updateKeyColors(str: String, hints: List<Int>) {
        val map = _keyColors.value!!.toMutableMap()
        val textMap = _keyTextColors.value!!.toMutableMap()
        for (i in 0..4) {
            val char = str[i]
            val color = hints[i]
            if (color == R.color.yellow && map[char] == R.color.green) {
                continue
            } else {
                map[char] = color
                textMap[char] = R.color.hint_text_color
            }
        }
        _keyColors.value = map.toMap()
        _keyTextColors.value = textMap.toMap()
    }

    // checks if the word is a valid word from the list
    fun isAWord(str: String): Int {
        Log.d("ViewModel", "isAWord() called")
        return when {
            str.length != 5 -> 0
            str in wordsList1 || str in wordsList2 -> 1
            else -> 2
        }
    }

    // checks each letter for whether it is correct, present or absent
// and creates a list of colors representing each state
    fun isLetterCorrect(str: String): List<Int> {
        Log.d("ViewModel", "isLetterCorrect() called")
        val hints = mutableListOf<Int>()
        for (i in (0..4)) {
            when {
                str[i] == _answer.value?.get(i) -> hints.add(R.color.green)
                str[i] in _answer.value!! -> hints.add(R.color.yellow)
                else -> hints.add(R.color.gray)
            }
        }
        return (hints)
    }

    // moves down a row and resets currentWord
    fun nextRow() {
        Log.d("ViewModel", "nextRow() called")
        _currentWord.value = ""
        _tries.value = _tries.value?.plus(1)
        updateGameState()
    }
}
