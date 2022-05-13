package com.example.wordle.ui.model

import android.app.Application
import androidx.lifecycle.*
import com.example.wordle.data.GameStateRepository
import com.example.wordle.data.StatsRepository
import java.util.*

/**
 * [StatsViewModel] is responsible for updating and exposing LiveData needed
 * to populate the Stats Dialog Fragment
 */
class StatsViewModel(app: Application) : AndroidViewModel(app) {

    private val statsRepo = StatsRepository(getApplication())

    private val gameStateRepo = GameStateRepository(getApplication())

    private val _playCount = MutableLiveData(getPlayCount())
    val playCount: LiveData<Int> = _playCount

    private val _winPercent = MutableLiveData(getWinPercentage())
    val winPercent: LiveData<String> = _winPercent

    private val _currentStreak = MutableLiveData(statsRepo.getCurrentStreak())
    val currentStreak: LiveData<Int> = _currentStreak

    private val _maxStreak = MutableLiveData(statsRepo.getMaxStreak())
    val maxStreak: LiveData<Int> = _maxStreak

    private val _wins = MutableLiveData(getWins())
    val wins: LiveData<List<Int>> = _wins

    private val _widths = MutableLiveData<List<Int>>()
    val widths: LiveData<List<Int>> = _widths

    /**
     * Sets the [_playCount] LiveData taking into account in progress games
     */
    private fun getPlayCount(): Int {
        var playCount = statsRepo.getPlayCount()
        if (gameStateRepo.getGameInProgress()) {
            playCount--
        }
        return playCount
    }

    /**
     * Calculates the [_winPercent] LiveData
     */
    private fun getWinPercentage(): String {
        val wins = statsRepo.getWinCount().toDouble()
        var plays = statsRepo.getPlayCount()
        if (gameStateRepo.getGameInProgress()) {
            plays--
        }
        return "%.1f".format(wins.div(plays.toDouble()).times(100))
    }

    /**
     * Returns the [_wins] LiveData
     */
    private fun getWins(): List<Int> {
        val wins = mutableListOf<Int>()
        for (i in 1..6) {
            wins.add(statsRepo.getWins(i))
        }
        return wins
    }

    /**
     * Calculates the [_widths] LiveData
     */
    fun calculateWidths(max: Float, min: Float) {
        val widths = mutableListOf<Int>()
        for (item in wins.value!!) {
            widths.add(
                item.toFloat()
                    .div(Collections.max(wins.value!!))
                    .times(max - min)
                    .plus(min).toInt()
            )
        }
        _widths.value = widths
    }
}