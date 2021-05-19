package com.tugraz.chronos

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.service.ChronosService
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    var chronosService: ChronosService = ChronosService(ApplicationProvider.getApplicationContext())
    val dummyTaskGroup: TaskGroup = TaskGroup("Dummy Task Group");
    val dummyTask: Task = Task(0, "TestTask", "TestDescirption", LocalDateTime.now().plusDays(1).toString())
    val modified_task: Task = Task(0, "ModifiedTitle", "ModifiedDesc", LocalDateTime.now().plusDays(2).toString())
    val dummyTaskWithGroup: Task = Task(0, "TaskWithGroup", "TaskWithGroupDescription", LocalDateTime.now().plusDays(2).toString())

    var dummy_id = 0
    var modified_id = 0

    @Before
    fun setUp() {
        val taskGrouprel = chronosService.addOrUpdateTaskGroup(dummyTaskGroup)
        dummyTaskWithGroup.groupId = taskGrouprel.taskGroup.taskGroupId
        chronosService.addOrUpdateTask(dummyTaskWithGroup)
        Intents.init()
        ActivityScenario.launch<MainActivity>(
                Intent(ApplicationProvider.getApplicationContext<Context>(),
                        MainActivity::class.java))
    }

    @After
    fun tearDown() {
        val groupList = chronosService.getAllGroups()

        groupList.forEach {chronosService.deleteGroupWithAllTasks(it.taskGroup)}
        Intents.release()
    }

    @Test
    fun testViews() {
        dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()
        modified_id = chronosService.addOrUpdateTask(modified_task).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown());

        var date1 = LocalDateTime.parse(
            dummyTask.date,
            DateTimeFormatter.ISO_DATE_TIME
        )
        var date2 = LocalDateTime.now()
        var input: Long = date2.until(date1, ChronoUnit.SECONDS)
        var days = input / 86400
        var hours = (input % 86400 ) / 3600
        var minutes = ((input % 86400 ) % 3600 ) / 60
        var seconds = ((input % 86400 ) % 3600 ) % 60

        val space = "    "
        var timeUntil = days.toString() + "d " + hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()
        var text = dummyTask.title + space + dummyTask.description + "\n" + timeUntil

        onView(withId(dummy_id)).check(matches(isDisplayed()))
        onView(withId(dummy_id)).check(matches(withText(text)))

        date1 = LocalDateTime.parse(
            modified_task.date,
            DateTimeFormatter.ISO_DATE_TIME
        )
        date2 = LocalDateTime.now()
        input = date2.until(date1, ChronoUnit.SECONDS)
        days = input / 86400
        hours = (input % 86400 ) / 3600
        minutes = ((input % 86400 ) % 3600 ) / 60
        seconds = ((input % 86400 ) % 3600 ) % 60
        timeUntil = days.toString() + "d " + hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()
        text = modified_task.title + space + modified_task.description + "\n" + timeUntil

        onView(withId(modified_id)).check(matches(isDisplayed()))
        onView(withId(modified_id)).check(matches(withText(text)))

        assert(chronosService.getAllTasks().size == 2)
        {"Couldn't insert all tasks."}
    }

    @Test
    fun testButton()  {
        onView(withId(R.id.btn_ma_add)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(CreateTaskActivity::class.java.name))
    }

    @Test
    fun testDrawerMenuVisible() {
        onView(withId(R.id.drawer_layout)).perform(open())
        onView(withId(R.id.nav_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testDrawerMenuWithDBGroups() {
        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())

        for (group in groups) {
            onView(withText(group.taskGroup.title))
        }
    }

    @Test
    fun testClickTaskGroup(){
        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(groups[0].taskGroup.title)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("GROUP_ID", groups[0].taskGroup.taskGroupId))
    }

    @Test
    fun testViewTaskGroupTasks(){
        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(groups[0].taskGroup.title)).perform(ViewActions.click())
        val tasksInGroup = groups[0].taskList
        for (task in tasksInGroup){
            onView(withText(task.title))
        }
    }
}