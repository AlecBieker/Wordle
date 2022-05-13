package com.example.wordle.repository_tests

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordle.MainActivity
import com.example.wordle.data.GameStateRepository
import com.example.wordle.data.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameStateRepositoryTests {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gameStateRepository: GameStateRepository

    private fun putTestValues() {
        with(sharedPreferences.gameState.edit()) {
            putString("answer", "TESTS")
            putInt("tries", 2)
            putString("letters", "TEST")
            putString("current_word", "TEST")
            putString("tile_states", "TEST")
            putString("key_states", "TEST")
            putString("transition", "TEST")
            apply()
        }
    }

    @Before
    fun setup() {
        launchActivity<MainActivity>()
        sharedPreferences = SharedPreferences(getApplicationContext())
        gameStateRepository = GameStateRepository(getApplicationContext())
    }

    @Test
    fun newGame_clears_gameState() {
        putTestValues()
        gameStateRepository.newGame("ANSWER")
        with(sharedPreferences.gameState) {
            assertEquals(getString("answer", ""), "ANSWER")
            assertEquals(getInt("tries", 0), 0)
            assertEquals(getString("letters", ""), "")
            assertEquals(getString("current_word", ""), "")
            assertEquals(getString("tile_states", ""), "")
            assertEquals(getString("transition", ""), "")
        }
    }
}