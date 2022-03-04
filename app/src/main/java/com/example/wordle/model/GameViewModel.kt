package com.example.wordle.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wordle.R
import com.example.wordle.wordsList1
import com.example.wordle.wordsList2

/**
 * [GameViewModel] holds all the variables for reinstating a saved game as well as all the logic
 * deciding what will be shown on the screen as the game is played
 */

class GameViewModel : ViewModel() {

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // values of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<String>>()
    val letters: LiveData<List<String>> = _letters

    // colors or drawable resources of the textView Backgrounds
    private val _backgrounds = MutableLiveData<List<Int>>()
    val backgrounds: LiveData<List<Int>> = _backgrounds

    // color of the textView texts
    private val _textColors = MutableLiveData<List<Int>>()
    val textColors: LiveData<List<Int>> = _textColors

    private val _keyColors = MutableLiveData<Map<Char, Int>>()
    val keyColors: LiveData<Map<Char, Int>> = _keyColors

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

    // Number of words the user has guess out of 6
    private val _tries = MutableLiveData<Int>()
    val tries: LiveData<Int> = _tries

    // resets all game parameters to the default values
    fun newGame() {
        Log.d("GameViewModel", "newGame() called")
        _answer.value = generate()
        _currentWord.value = ""
        _tries.value = 0
        _letters.value = listOf()
        _backgrounds.value = listOf()
        _textColors.value = listOf()
        _keyColors.value = mapOf()
        _keyTextColors.value = mapOf()
    }

    // updates the letters and currentWord val
    fun updateText(char: Char) {
        Log.d("GameViewModel", "setText($char) called")
        val size = _currentWord.value!!.length
        if (char == ' ' && size != 0) {
            _letters.value = _letters.value?.dropLast(1)
            _backgrounds.value = _backgrounds.value?.dropLast(1)
            _currentWord.value = _currentWord.value?.dropLast(1)
        } else if (char != ' ' && size != 5) {
            _letters.value = _letters.value?.plus(char.toString())
            _backgrounds.value = _backgrounds.value?.plus(R.drawable.filled_border)
            _currentWord.value += char
        }
    }

    // Clears the contents of the current row
    fun clearRow(): Boolean {
        Log.d("GameViewModel", "clearRow() called")
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
        Log.d("GameViewModel", "isAWord() called")
        return when {
            str.length != 5 -> 0
            str in wordsList1 || str in wordsList2 -> 1
            else -> 2
        }
    }

    // checks each letter for whether it is correct, present or absent
    // and creates a list of colors representing each state
    fun isLetterCorrect(str: String): List<Int> {
        Log.d("GameViewModel", "isLetterCorrect() called")
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
        Log.d("GameViewModel", "nextRow() called")
        _currentWord.value = ""
        _tries.value = _tries.value?.plus(1) ?: 1
    }

    // generates a new word at random from wordsList
    private fun generate(): String {
        Log.d("GameViewModel", "generate() called")
        return (wordsList1 + wordsList2).random()
    }
}
