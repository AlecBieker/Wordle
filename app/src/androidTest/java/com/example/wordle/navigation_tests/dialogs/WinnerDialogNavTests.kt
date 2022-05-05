package com.example.wordle.navigation_tests.dialogs

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordle.R
import com.example.wordle.dialogs.WinnerDialog
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WinnerDialogNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var winnerDialogScenario: FragmentScenario<WinnerDialog>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Launch dialog
        winnerDialogScenario = launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        winnerDialogScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.winnerDialog)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun play_again_button_displays_game_fragment() {
        onView(withId(R.id.play_again_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.gameFragment)
    }

    @Test
    fun quit_game_button_displays_start_fragment() {
        onView(withId(R.id.quit_game_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.startFragment)
    }
}