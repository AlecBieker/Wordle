package com.example.wordle.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wordle.R
import com.example.wordle.wordsList1
import com.example.wordle.wordsList2

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

    val border = R.drawable.border

    // Last word to be guessed
    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    // Number of words the user has guess out of 6
    private val _tries = MutableLiveData<Int>()
    val tries: LiveData<Int> = _tries

    // Current Tile for input
    private val _currentTile = MutableLiveData<Int>()
    val currentTile: LiveData<Int> = _currentTile

    // resets all game parameters to the default values
    fun newGame() {
        Log.d("GameViewModel", "newGame() called")
        _answer.value = generate()
        _currentWord.value = ""
        _currentTile.value = 1
        _tries.value = 0
        _letters.value = listOf()
        _colors.value = listOf()
    }

    // updates the letters and currentWord val
    fun setText(char: Char) {
        Log.d("GameViewModel", "updateLetters($char) called")
        val size = _letters.value!!.size
        if (char == ' ' && size != _tries.value?.times(5)) {
            _letters.value = _letters.value?.dropLast(1)
            _currentWord.value?.dropLast(1)
        } else if (char != ' ' &&
            (size.rem(5) != 0 || size == 0)){
            _letters.value = _letters.value?.plus(char.toString())
            _currentWord.value += char
        }
    }

    // Clears the contents of the current row
    fun clearRow(): Boolean {
        Log.d("GameViewModel", "clearRow() called")
        val size = _letters.value!!.size
        if (tries.value?.times(5) != size) {
            _letters.value = _letters.value?.dropLast(5)
        } else {
            _letters.value = _letters.value?.dropLast(size.rem(5))
        }
        return false
    }

    // moves down a row and resets currentWord
    fun nextRow() {
        Log.d("GameViewModel", "nextRow() called")
        _currentTile.value = _currentTile.value?.plus(1)
        _currentWord.value = ""
        _tries.value = _tries.value?.plus(1) ?: 1
    }

    // generates a new word at random from wordsList
    private fun generate(): String {
        Log.d("GameViewModel", "generate() called")
        return (wordsList1 + wordsList2).random()
    }

    // goes back a tile
    fun lastTile() {
        Log.d("GameViewModel", "lastTile() called")
        _currentTile.value = _currentTile.value!!.minus(1)
    }

    // goes forward a tile
    fun nextTile() {
        Log.d("GameViewModel", "nextTile() called")
        if (_currentTile.value!! % 6 != 0) {
            _currentTile.value = _currentTile.value!!.plus(1)
        }
    }
}
