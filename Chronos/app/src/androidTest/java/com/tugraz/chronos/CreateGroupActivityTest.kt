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
class CreateGroupActivityTest {
    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch<CreateTaskActivity>(
            Intent(ApplicationProvider.getApplicationContext<Context>(),
                CreateGroupActivity::class.java))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testViews()  {
        onView(withId(R.id.group_name)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_create_group)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_create_group)).check(matches(isDisplayed()))
    }

    @Test
    fun testSuccess()  {
        onView(withId(R.id.group_name)).perform(typeText("TestGroup"),
            closeSoftKeyboard())
        onView(withId(R.id.btn_create_group)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}