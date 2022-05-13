package com.example.wordle.navigation_tests.fragments

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
import com.example.wordle.ui.fragments.SettingsFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsFragmentNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var settingsScenario: FragmentScenario<SettingsFragment>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Launch Settings Fragment
        settingsScenario =
            launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        settingsScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.settingsFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun reset_button_displays_reset_dialog() {
        onView(withId(R.id.reset_button)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.resetDialog)
    }
}