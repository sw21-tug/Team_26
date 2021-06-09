package com.tugraz.chronos

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.icu.text.Transliterator
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.close
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.contrib.DrawerMatchers
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
import java.lang.Thread.sleep
import java.sql.Time
import java.lang.Thread.sleep
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

    var now = LocalDateTime.now()
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
        now = LocalDateTime.now()

        val groups = chronosService.getAllGroups()
        onView(withId(R.id.drawer_layout)).perform(open())

        assert(groups.isNotEmpty())

        onView(withText(dummyTaskGroup.title + "\n" +
                dummyTaskWithGroup.title + " - " +
                getTimeUntil(dummyTaskWithGroup, now))).perform(ViewActions.click())
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
        onView(withId(R.id.rv_ma))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskItemHolder>(0,
                GeneralSwipeAction(
                    Swipe.SLOW, GeneralLocation.CENTER_LEFT , GeneralLocation.CENTER_RIGHT, Press.FINGER
                )))
        onView(withId(R.id.drawer_layout)).check(matches(DrawerMatchers.isOpen()))
        assert(groups.isNotEmpty())

        onView(withText(dummyTaskWithGroup.title)).perform(click())
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
        onView(withId(R.id.drawer_layout)).perform(close())
        onView(withId(R.id.srl_ma)).perform(swipeDown())
        now = LocalDateTime.now()

        onView(withId(R.id.drawer_layout)).perform(open())
        assert(groups.isNotEmpty())

        onView(withText(getTimeUntil(sortedTaskOne, now))).check(matches(isDisplayed()))
        onView(withText(getTimeUntil(sortedTaskTwo, now))).check(matches(isDisplayed()))
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

    @Test
    fun testEdit() {
        val test_string = "Test Title"
        val dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.rv_ma))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskItemHolder>(0,
                GeneralSwipeAction(
                    Swipe.FAST, GeneralLocation.VISIBLE_CENTER , GeneralLocation.CENTER_RIGHT, Press.FINGER
                )))

        sleep(1000)
        Intents.intended(IntentMatchers.hasComponent(CreateTaskActivity::class.java.name))

        onView(withId(R.id.tv_ct_title)).check(matches(withText(R.string.save_task)))
        onView(withId(R.id.et_ct_title)).check(matches(withText(dummyTask.title)))
        onView(withId(R.id.et_ct_date)).check(matches(withText(dummyTask.date)))
        onView(withId(R.id.et_ct_description)).check(matches(withText(dummyTask.description)))
        onView(withId(R.id.btn_ct_save)).check(matches(withText(R.string.save)))

        //edit title and save
        onView(withId(R.id.et_ct_title)).perform(clearText())
        onView(withId(R.id.et_ct_title)).perform(typeText(test_string),
            closeSoftKeyboard())
        onView(withId(R.id.btn_ct_save)).perform(click())


        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rv_ma))
            .check(matches(atPosition(0, hasDescendant(withText(test_string)))))

        assert(chronosService.getAllTasks().size > 0)
        {"Edit went wrong"}
    }

    @Test
    fun testClickTaskDetails() {
        val dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.rv_ma))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskItemHolder>(0,
                ViewActions.click()))

        Intents.intended(IntentMatchers.hasComponent(TaskDetailsActivity::class.java.name))
        Intents.intended(IntentMatchers.hasExtra("id", dummy_id))
    }
}