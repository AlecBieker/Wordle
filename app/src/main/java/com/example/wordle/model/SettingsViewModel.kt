package com.example.wordle.model

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.wordle.data.SharedPrefs
import com.example.wordle.worker.NotificationWorker
import java.util.concurrent.TimeUnit

/**
 * [SettingsViewModel] controls all of the settings and holds references to
 * [SharedPrefs] for handling persistent data and acting as an access point
 * for the Activity and Fragments.
 */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    // shared preferences handle for saved game state
    val gameState = SharedPrefs(getApplication()).gameState

    // shared preferences handle for game stats
    val stats = SharedPrefs(getApplication()).stats

    // shared preferences handle for user settings
    val settings = SharedPrefs(getApplication()).settings

    // Instance of WorkManager for notifications
    private val workManager = WorkManager.getInstance(getApplication())

    /**
     * Sets the theme for the app
     */
    fun setTheme(theme: Int) {
        Log.d("SettingsViewModel", "setTheme() called")
        settings.edit().putInt("theme", theme).apply()
        AppCompatDelegate.setDefaultNightMode(theme)
    }

    /**
     * Turns hard mode on or off
     */
    fun setHardMode(hardMode: Boolean) {
        Log.d("SettingsViewModel", "toggleHardMode($hardMode) called")
        settings.edit().putBoolean("hard_mode", hardMode).apply()
    }

    /**
     * Turns Notifications on and off
     */
    fun setNotifications(isChecked: Boolean) {
        Log.d("SettingsViewModel", "setNotifications() called")
        settings.edit().putBoolean("notifications", isChecked).apply()
        if (!isChecked) {
            cancelNotifications()
        }
    }

    /**
     * Ques a periodicWorkRequest with [NotificationWorker]
     */
    fun queNotifications() {
        Log.d("SettingsViewModel", "queNotifications() called")
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
        Log.d("SettingsViewModel", "cancelNotifications() called")
        workManager.cancelUniqueWork("notifications")
    }

    /**
     * Resets user statistics
     */
    fun resetStats() {
        Log.d("SettingsViewModel", "resetStats() called")
        stats.edit().clear().apply()
        gameState.edit().clear().apply()
    }

    /**
     * Calculates the win percentage
     */
    fun getWinPercentage(plays: Int): String {
        val wins = stats.getInt("win_count", 0).toDouble()
        return "%.1f".format(wins.div(plays.toDouble()).times(100))
    }

}