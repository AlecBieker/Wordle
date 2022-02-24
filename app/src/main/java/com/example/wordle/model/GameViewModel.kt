package com.example.wordle.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wordle.wordsList1
import com.example.wordle.wordsList2

class GameViewModel : ViewModel() {

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // value of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<String>?>()
    val letters: LiveData<List<String>?> = _letters

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
    }

    // updates the _letters MutableLiveData val to reflect user inputs
    fun updateLetters(char: Char) {
        val size = _letters.value!!.size
        if (char == ' ' && size != _tries.value?.times(5)) {
            _letters.value = _letters.value?.dropLast(1)
        } else if (char != ' ' &&
            (size.rem(5) != 0 || size == 0)){
            _letters.value = _letters.value?.plus(char.toString())
        }
    }

    // Clears the contents of the current row
    fun clearRow(): Boolean {
        val size = _letters.value!!.size
        if (tries.value?.times(5) != size) {
            _letters.value = _letters.value?.dropLast(5)
        } else {
            _letters.value = _letters.value?.dropLast(size.rem(5))
        }
        return false
    }

    // updates the currentWord variable
    fun updateCurrentWord(char: Char) {
        Log.d("GameViewModel", "updateCurrentWord($char) called")
        if (char == ' ') {
            _currentWord.value = _currentWord.value?.dropLast(1)
        } else {
            _currentWord.value += char
        }
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
