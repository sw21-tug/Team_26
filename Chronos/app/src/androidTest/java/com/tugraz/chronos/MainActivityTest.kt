package com.tugraz.chronos

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tugraz.chronos.model.entities.Task
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.service.ChronosService
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
    return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    val now = LocalDateTime.now()
    var chronosService: ChronosService = ChronosService(ApplicationProvider.getApplicationContext())
    val dummyTaskGroup: TaskGroup = TaskGroup("Dummy Task Group")
    val dummyTask: Task = Task(0, "TestTask", "TestDescription", now.plusDays(1).toString())
    val modified_task: Task = Task(0, "ModifiedTitle", "ModifiedDesc", now.plusDays(2).toString())
    val dummyTaskWithGroup: Task = Task(0, "TaskWithGroup", "TaskWithGroupDescription", LocalDateTime.now().plusDays(2).toString())

    val sortedGroupOne: TaskGroup = TaskGroup("SortedGroupOne")
    val sortedGroupTwo: TaskGroup = TaskGroup("SortedGroupTwo")
    val sortedTaskOne: Task = Task(0, "sortedTaskOne", "sortedTaskOne", now.plusDays(1).toString())
    val sortedTaskTwo: Task = Task(0, "sortedTaskTwo", "sortedTaskTwo", now.plusDays(2).toString())


    @Before
    fun setUp() {
        Intents.init()
        ActivityScenario.launch<MainActivity>(
                Intent(ApplicationProvider.getApplicationContext<Context>(),
                        MainActivity::class.java))
    }

    @After
    fun tearDown() {
        val taskList = chronosService.getAllTasks()
        taskList.forEach { chronosService.deleteTask(it) }

        val groupList = chronosService.getAllGroups()
        groupList.forEach { chronosService.deleteTaskGroup(it.taskGroup) }

        Intents.release()
    }

    @Test
    fun testViews() {
        val dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()
        val modified_id = chronosService.addOrUpdateTask(modified_task).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.rv_ma))
                .check(matches(atPosition(0, hasDescendant(withText(dummyTask.title)))))

        onView(withId(R.id.rv_ma))
                .check(matches(atPosition(1, hasDescendant(withText(modified_task.title)))))
    }

    @Test
    fun testButton() {
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
    fun testClickTaskGroup() {
        val taskGrouprel = chronosService.addOrUpdateTaskGroup(dummyTaskGroup)
        dummyTaskWithGroup.groupId = taskGrouprel.taskGroup.taskGroupId
        chronosService.addOrUpdateTask(dummyTaskWithGroup)
        onView(withId(R.id.srl_ma)).perform(swipeDown())
        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(groups[0].taskGroup.title)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("GROUP_ID", groups[0].taskGroup.taskGroupId.toInt()))
    }

    @Test
    fun testViewTaskGroupTasks() {
        val taskGrouprel = chronosService.addOrUpdateTaskGroup(dummyTaskGroup)
        dummyTaskWithGroup.groupId = taskGrouprel.taskGroup.taskGroupId
        chronosService.addOrUpdateTask(dummyTaskWithGroup)
        onView(withId(R.id.srl_ma)).perform(swipeDown())

        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(groups[0].taskGroup.title)).perform(ViewActions.click())
        val tasksInGroup = groups[0].taskList
        for (task in tasksInGroup) {
            onView(withText(task.title))
        }
    }

    @Test
    fun testSortedGroups() {
        val group2 = chronosService.addOrUpdateTaskGroup(sortedGroupTwo)
        val group1 = chronosService.addOrUpdateTaskGroup(sortedGroupOne)
        onView(withId(R.id.srl_ma)).perform(swipeDown())

        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(groups[0].taskGroup.title)).check(matches(isDisplayed()))
        onView(withText(groups[1].taskGroup.title)).check(matches(isDisplayed()))

        sortedTaskOne.groupId = group1.taskGroup.taskGroupId
        sortedTaskTwo.groupId = group2.taskGroup.taskGroupId
        chronosService.addOrUpdateTask(sortedTaskOne)
        chronosService.addOrUpdateTask(sortedTaskTwo)
        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        var date1 = LocalDateTime.parse(
            sortedTaskOne.date,
            DateTimeFormatter.ISO_DATE_TIME
        )
        var input: Long = now.until(date1, ChronoUnit.SECONDS)

        var days = input / 86400
        var hours = (input % 86400 ) / 3600
        var minutes = ((input % 86400 ) % 3600 ) / 60
        var seconds = ((input % 86400 ) % 3600 ) % 60
        var timeUntil = days.toString() + "d " + hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()

        val text1 = groups[0].taskGroup.title + "\n" + sortedTaskOne.title + "\n" + timeUntil

        date1 = LocalDateTime.parse(
            sortedTaskTwo.date,
            DateTimeFormatter.ISO_DATE_TIME
        )
        input = now.until(date1, ChronoUnit.SECONDS)

        days = input / 86400
        hours = (input % 86400 ) / 3600
        minutes = ((input % 86400 ) % 3600 ) / 60
        seconds = ((input % 86400 ) % 3600 ) % 60
        timeUntil = days.toString() + "d " + hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()

        val text2 = groups[0].taskGroup.title + "\n" + sortedTaskOne.title + "\n" + timeUntil

        onView(withText(text1)).check(matches(isDisplayed()))
        onView(withText(text2)).check(matches(isDisplayed()))
    }

    @Test
    fun testDelete() {
        val dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()
        val modified_id = chronosService.addOrUpdateTask(modified_task).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.rv_ma))
                .perform(RecyclerViewActions.actionOnItemAtPosition<TaskItemHolder>(0,
                        GeneralSwipeAction(
                                Swipe.SLOW, GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER
                        )))

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        // Only modified_task should be in the list and at pos 0
        onView(withId(R.id.rv_ma))
                .check(matches(atPosition(0, hasDescendant(withText(modified_task.title)))))

        assert(chronosService.getAllTasks().size == 1)
        { "Task couldn't be deleted." }
    }
}