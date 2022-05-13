package com.example.wordle.ui.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.wordle.data.GameStateRepository
import com.example.wordle.data.SettingsRepository
import com.example.wordle.data.StatsRepository
import com.example.wordle.data.WordsList1.wordsList1
import com.example.wordle.data.WordsList2.wordsList2
import org.jetbrains.annotations.TestOnly
import kotlin.random.Random

enum class ViewState { FILLED, CORRECT, PRESENT, ABSENT }

/**
 * [GameViewModel] holds variables representing the games current state as well as
 * all the logic controlling how those variables are modified based on user input.
 */
class GameViewModel(app: Application) : AndroidViewModel(app) {

    // handle to the SharedPrefsRepository
    private val gameStateRepo = GameStateRepository(getApplication())

    private val settingsRepo = SettingsRepository(getApplication())

    private val statsRepo = StatsRepository(getApplication())

    // Generated word to be guessed
    private val _answer = MutableLiveData<String>()
    val answer: LiveData<String> = _answer

    // values of all the keyboard inputs so far
    private val _letters = MutableLiveData<List<Char>>()
    val letters: LiveData<List<Char>> = _letters

    // the states of the game tiles as represented by enum class ViewState
    private val _tileStates = MutableLiveData<List<ViewState>>()
    val tileStates: LiveData<List<ViewState>> = _tileStates

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

    // Whether hard mode is on or not
    private val _hardMode = MutableLiveData<Boolean>()
    val hardMode: LiveData<Boolean> = _hardMode

    // Whether a game is currently in progress or not
    private val _gameInProgress = MutableLiveData(gameStateRepo.getGameInProgress())
    val gameInProgress: LiveData<Boolean> = _gameInProgress

    /**
     * Initiates a new game
     */
    fun newGame() {
        Log.d("GameViewModel", "newGame() called")
        resetVariables()
        _answer.value = generate()
        _transition.value = null
        _hardMode.value = settingsRepo.getHardMode()
        _gameInProgress.value = true
        gameStateRepo.newGame(answer.value!!)
        statsRepo.setPlayCount()
        Log.d("GameViewModel", "answer = ${answer.value}")
    }

    /**
     * Resets all game parameters to the default values
     */
    private fun resetVariables() {
        Log.d("GameViewModel", "resetVariables() called")
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
        Log.d("GameViewModel", "generate() called")
        val rand = Random(System.nanoTime())
        return (wordsList1 + wordsList2).random(rand)
    }

    /**
     * Sets all LiveData variables using [GameStateRepository]
     */
    fun resumeGame() {
        Log.d("GameViewModel", "resumeGame() called")
        resetVariables()
        _answer.value = gameStateRepo.getAnswer()
        _tries.value = gameStateRepo.getTries()
        _currentWord.value = gameStateRepo.getCurrentWord()
        _transition.value = gameStateRepo.getTransition()
        _letters.value = gameStateRepo.getLetters()
        _tileStates.value = gameStateRepo.getTileStates()
        _keyStates.value = gameStateRepo.getKeyStates()
        _hardMode.value = settingsRepo.getHardMode()
    }

    /**
     * Updates the gameState SharedPreferences with [GameStateRepository]
     */
    fun updateGameState() {
        Log.d("GameViewModel", "updateGameState() called")
        gameStateRepo.updateGameState(
            tries = tries.value ?: 0,
            letters = letters.value,
            currentWord = currentWord.value,
            tileStates = tileStates.value,
            keyStates = keyStates.value ?: mapOf(),
            transition = transition.value
        )
    }

    /**
     * Updates the [_gameInProgress] LiveData
     */
    fun updateGameInProgress() {
        _gameInProgress.value = gameStateRepo.getGameInProgress()
    }

    /**
     * Ends the game and updates stats SharedPreferences with [GameStateRepository]
     */
    fun endGame(winner: Boolean) {
        gameStateRepo.setGameInProgress(false)
        _gameInProgress.value = false
        statsRepo.updateStats(winner, (tries.value ?: 0).plus(1))
    }

    /**
     * Updates the [_letters], [_currentWord], and [_tileStates] values using keyboard input
     */
    fun updateText(char: Char) {
        Log.d("GameViewModel", "setText($char) called")
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
        Log.d("GameViewModel", "clearRow() called")
        val size = _currentWord.value!!.length
        _letters.value = _letters.value?.dropLast(size)
        _tileStates.value = _tileStates.value?.dropLast(size)
        _currentWord.value = ""
        return true
    }

    /**
     * Updates the [_transition] live data variable
     */
    fun updateTransition(transition: String?) {
        Log.d("GameViewModel", "updateTransition() called")
        _transition.value = transition
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
        Log.d("GameViewModel", "isUsingHints() called")
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
        Log.d("GameViewModel", "isAWord() called")
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
        Log.d("GameViewModel", "isLetterCorrect() called")
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
        Log.d("GameViewModel", "isCorrect() called")
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
     * Moves down a row, resets [_currentWord] and saves progress to [gameStateRepo]
     */
    private fun nextRow() {
        Log.d("GameViewModel", "nextRow() called")
        _currentWord.value = ""
        _tries.value = _tries.value?.plus(1)
        updateGameState()
    }

    /**
     * Changes the answer for testing purposes
     */
    @TestOnly
    fun changeAnswer(word: String) {
        _answer.value = word
    }
}
