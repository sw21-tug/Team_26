package com.tugraz.chronos

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


class ElapsedTimeIdlingResource(waitingTime: Long) : IdlingResource {
    private val startTime: Long
    private val waitingTime: Long
    private var resourceCallback: ResourceCallback? = null

    override fun getName(): String {
        return ElapsedTimeIdlingResource::class.java.name + ":" + waitingTime
    }

    override fun isIdleNow(): Boolean {
        val elapsed = System.currentTimeMillis() - startTime
        val idle = elapsed >= waitingTime
        if (idle) {
            resourceCallback!!.onTransitionToIdle()
        }
        return idle
    }

    override fun registerIdleTransitionCallback(resourceCallback: ResourceCallback) {
        this.resourceCallback = resourceCallback
    }

    init {
        startTime = System.currentTimeMillis()
        this.waitingTime = waitingTime
    }
}

@RunWith(AndroidJUnit4::class)
class SplashActivityTest {

    private val splashScreenWaitingTime : Long = 6000
    private lateinit var idlingResource : ElapsedTimeIdlingResource

    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch<SplashActivity>(
            Intent(ApplicationProvider.getApplicationContext<Context>(),
            SplashActivity::class.java))
        idlingResource = ElapsedTimeIdlingResource(splashScreenWaitingTime)
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        Intents.release()
    }

    @Test
    fun showMainActivityAfterSplashActivity()  {
        intended(hasComponent(MainActivity::class.java.name))
    }

}