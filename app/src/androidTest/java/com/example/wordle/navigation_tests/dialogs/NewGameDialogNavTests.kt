package com.example.wordle.navigation_tests.dialogs

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordle.R
import com.example.wordle.dialogs.NewGameDialog
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewGameDialogNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var newGameDialogScenario: FragmentScenario<NewGameDialog>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Launch dialog
        newGameDialogScenario = launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        newGameDialogScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.newGameDialog)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun start_new_game_button_displays_game_fragment() {
        onView(withId(R.id.start_new_game_button)).perform(click())
        Assert.assertEquals(navController.currentDestination?.id, R.id.gameFragment)
    }

    @Test
    fun cancel_button_dismisses_dialog() {
        onView(withId(R.id.cancel_button)).perform(click())
        onView(withId(R.id.are_you_sure_textView)).check(doesNotExist())
    }
}