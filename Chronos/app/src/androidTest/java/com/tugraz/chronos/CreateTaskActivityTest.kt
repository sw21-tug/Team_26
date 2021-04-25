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
class CreateTaskActivityTest {
    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch<CreateTaskActivity>(
            Intent(ApplicationProvider.getApplicationContext<Context>(),
                CreateTaskActivity::class.java))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testViews()  {
        onView(withId(R.id.et_ct_title)).check(matches(isDisplayed()))
        onView(withId(R.id.et_ct_date)).check(matches(isDisplayed()))
        onView(withId(R.id.et_ct_description)).check(matches(isDisplayed()))
        onView(withId(R.id.sp_ct_group)).check(matches(isDisplayed()))
        onView(withId(R.id.cl_ct)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_ct_save)).check(matches(isDisplayed()))
    }

    @Test
    fun testTitleError() {
        onView(withId(R.id.btn_ct_save)).perform(click())
        onView(withText(R.string.err_title_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun testDateError() {
        onView(withId(R.id.et_ct_title)).perform(typeText("TestTitle"),
                                                 closeSoftKeyboard())
        onView(withId(R.id.btn_ct_save)).perform(click())
        onView(withText(R.string.err_date_empty)).check(matches(isDisplayed()))
    }

    @Test
    fun testSuccess()  {
        onView(withId(R.id.et_ct_title)).perform(typeText("TestTitle"),
            closeSoftKeyboard())
        onView(withId(R.id.et_ct_date)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2021, 4, 21))
        onView(withId(android.R.id.button1)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(20, 10))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.btn_ct_save)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}