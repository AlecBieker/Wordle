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
 * behind what will be shown on the screen as the game is played
 */

class GameViewModel : ViewModel() {

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // values of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<String>>()
    val letters: LiveData<List<String>> = _letters

    // color of the textView Backgrounds
    private val _colors = MutableLiveData<List<Int>>()
    val colors: LiveData<List<Int>> = _colors

    // border drawable for textViews that don't have a color set yet
    val border = R.drawable.border

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
        _colors.value = listOf()
    }

    // updates the letters and currentWord val
    fun setText(char: Char) {
        Log.d("GameViewModel", "setText($char) called")
        val size = _currentWord.value!!.length
        if (char == ' ' && size != 0) {
            _letters.value = _letters.value?.dropLast(1)
            _currentWord.value = _currentWord.value?.dropLast(1)
        } else if (char != ' ' && size != 5){
            _letters.value = _letters.value?.plus(char.toString())
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
    fun updateColors(hint: Int) {
        _colors.value = _colors.value!!.plus(hint)
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
                else -> hints.add(R.color.darkGray)
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
