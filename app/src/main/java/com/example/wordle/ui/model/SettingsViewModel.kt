package com.example.wordle.ui.model

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.wordle.data.GameStateRepository
import com.example.wordle.data.SettingsRepository
import com.example.wordle.data.StatsRepository
import com.example.wordle.worker.NotificationWorker
import java.util.concurrent.TimeUnit

/**
 * [SettingsViewModel] handles changes to the settings and exposes liveData variables
 * to the Settings Fragment, also
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val settingsRepo = SettingsRepository(getApplication())

    private val statsRepo = StatsRepository(getApplication())

    private val gameStateRepo = GameStateRepository(getApplication())

    // Instance of WorkManager for notifications
    private val workManager = WorkManager.getInstance(getApplication())

    private val _theme = MutableLiveData(settingsRepo.getTheme())
    val theme: LiveData<Int> = _theme

    private val _hardMode = MutableLiveData(settingsRepo.getHardMode())
    val hardMode: LiveData<Boolean> = _hardMode

    private val _notifications = MutableLiveData(settingsRepo.getNotifications())
    val notifications: LiveData<Boolean> = _notifications

    /**
     * Sets the theme for the app
     */
    fun setTheme(theme: Int) {
        Log.d("SettingsViewModel", "setTheme() called")
        settingsRepo.setTheme(theme)
        AppCompatDelegate.setDefaultNightMode(theme)
        _theme.value = settingsRepo.getTheme()
    }

    /**
     * Turns hard mode on or off
     */
    fun setHardMode(isChecked: Boolean) {
        Log.d("SettingsViewModel", "setHardMode($isChecked) called")
        settingsRepo.setHardMode(isChecked)
        _hardMode.value = settingsRepo.getHardMode()
    }

    /**
     * Turns Notifications on and off
     */
    fun setNotifications(isChecked: Boolean) {
        Log.d("SettingsViewModel", "setNotifications() called")
        settingsRepo.setNotifications(isChecked)
        if (!isChecked) {
            cancelNotifications()
        }
        _notifications.value = settingsRepo.getNotifications()
    }

    /**
     * Ques a periodicWorkRequest with [NotificationWorker]
     */
    fun queNotifications() {
        Log.d("SettingsViewModel", "queNotifications() called")
        if (settingsRepo.getNotifications()) {
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
        Log.d("SettingsViewModel", "cancelNotifications() called")
        workManager.cancelUniqueWork("notifications")
    }

    /**
     * Resets user statistics and saved game state
     */
    fun reset() {
        Log.d("SettingsViewModel", "resetStats() called")
        statsRepo.resetStats()
        gameStateRepo.resetGameState()
    }
}