package com.tugraz.chronos

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class OptionsActivityTest {

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch<MainActivity>(
                Intent(ApplicationProvider.getApplicationContext<Context>(),
                        MainActivity::class.java))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testViews() {
        onView(withId(R.id.change_language)).check(matches(isDisplayed()))
        onView(withId(R.id.title_options)).check(matches(isDisplayed()))
    }

    @Test
    fun checkCurrentLanguage () {

    }

    @Test
    fun testLanguageChange() {
        onView(withId(R.id.change_language)).perform(click())

    }
}