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
import com.example.wordle.fragments.GameFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests Navigation between screens originating from the Game Fragment
 */
@RunWith(AndroidJUnit4::class)
class GameFragmentNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var gameScenario: FragmentScenario<GameFragment>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(getApplicationContext())
        // Launch Game Fragment
        gameScenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        gameScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.gameFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun help_icon_navigates_to_help_fragment() {
        onView(withId(R.id.help_icon)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.helpDialog)
    }

    @Test
    fun stats_icon_navigates_to_stats_dialog() {
        onView(withId(R.id.stats_icon)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.statsDialog)
    }
}