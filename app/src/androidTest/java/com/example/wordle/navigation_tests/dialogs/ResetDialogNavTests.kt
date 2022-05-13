
package com.example.wordle.navigation_tests.dialogs

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wordle.R
import com.example.wordle.ui.dialogs.ResetDialog
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResetDialogNavTests {

    private lateinit var navController: TestNavHostController
    private lateinit var resetDialogScenario: FragmentScenario<ResetDialog>

    @Before
    fun setup() {
        // Init nav controller
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        // Launch dialog
        resetDialogScenario = launchFragmentInContainer(themeResId = R.style.Theme_Wordle)
        // Configure nav controller
        resetDialogScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(destId = R.id.resetDialog)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun reset_stats_button_dismisses_dialog() {
        onView(withId(R.id.reset_stats_button)).perform(click())
        onView(withId(R.id.are_you_sure_textView)).check(ViewAssertions.doesNotExist())
    }

    @Test
    fun cancel_button_dismisses_dialog() {
        onView(withId(R.id.cancel_button)).perform(click())
        onView(withId(R.id.are_you_sure_textView)).check(ViewAssertions.doesNotExist())
    }
}