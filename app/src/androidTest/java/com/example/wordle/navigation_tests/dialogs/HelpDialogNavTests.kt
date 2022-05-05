

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
import com.example.wordle.dialogs.HelpDialog
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HelpDialogNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var helpDialogScenario: FragmentScenario<HelpDialog>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Launch dialog
        helpDialogScenario = launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        helpDialogScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.helpDialog)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun close_button_dismisses_dialog() {
        onView(withId(R.id.close_button)).perform(click())
        onView(withId(R.id.help_dialog_layout)).check(doesNotExist())
    }
}