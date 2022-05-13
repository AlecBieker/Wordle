package com.example.wordle.data

import android.content.Context

/**
 * Class responsible for communicating with the [settings] SharedPreferences
 */
class SettingsRepository(context: Context) {

    private val settings = SharedPreferences(context).settings

    fun getHardMode(): Boolean = settings.getBoolean("hard_mode", false)

    fun getNotifications(): Boolean = settings.getBoolean("notifications", true)

    fun getTheme(): Int = settings.getInt("theme", -1)

    fun setTheme(theme: Int) {
        settings.edit().putInt("theme", theme).apply()
    }

    fun setHardMode(hardMode: Boolean) {
        settings.edit().putBoolean("hard_mode", hardMode).apply()
    }

    fun setNotifications(notifications: Boolean) {
        settings.edit().putBoolean("notifications", notifications).apply()
    }
}