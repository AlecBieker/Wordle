package com.example.wordle.navigation_tests.fragments

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordle.R
import com.example.wordle.ui.fragments.StartFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests Navigation between screens originating from the Start Fragment
 */
@RunWith(AndroidJUnit4::class)
class StartFragmentNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var startScenario: FragmentScenario<StartFragment>
    private val gameState: android.content.SharedPreferences =
        com.example.wordle.data.SharedPreferences(getApplicationContext()).gameState
    private val gameInProgress: Boolean = gameState.getBoolean("game_in_progress", false)

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(getApplicationContext())
        // Launch Start Fragment
        startScenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        startScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.startFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun resume_game_button_displays_game_fragment() {
        gameState.edit().putBoolean("game_in_progress", true).apply()
        onView(withId(R.id.resume_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.gameFragment)
        gameState.edit().putBoolean("game_in_progress", gameInProgress).apply()
    }

    @Test
    fun new_game_button_displays_new_game_dialog() {
        gameState.edit().putBoolean("game_in_progress", true).apply()
        onView(withId(R.id.new_game_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.newGameDialog)
        gameState.edit().putBoolean("game_in_progress", gameInProgress).apply()
    }

    @Test
    fun new_game_button_displays_game_fragment() {
        gameState.edit().putBoolean("game_in_progress", false).apply()
        onView(withId(R.id.new_game_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.gameFragment)
        gameState.edit().putBoolean("game_in_progress", gameInProgress).apply()
    }

    @Test
    fun view_stats_button_displays_stats_dialog() {
        onView(withId(R.id.view_stats_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.statsDialog)
    }

    @Test
    fun how_to_play_button_displays_help_dialog() {
        onView(withId(R.id.how_to_play_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.helpDialog)
    }

    @Test
    fun settings_button_displays_settings_fragment() {
        onView(withId(R.id.settings_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.settingsFragment)
    }

}