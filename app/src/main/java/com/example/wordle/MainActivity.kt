package com.example.wordle

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.wordle.model.GameViewModel

/**
 * This is the activity that all of the apps fragments take place inside
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var navController: NavController

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate() called")
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(viewModel.settings.getInt("theme", -1))

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}