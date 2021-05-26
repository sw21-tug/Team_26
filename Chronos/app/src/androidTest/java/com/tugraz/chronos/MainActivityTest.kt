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
import com.tugraz.chronos.model.database.ChronosDB
import com.tugraz.chronos.model.entities.Task
import kotlinx.coroutines.runBlocking
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

        val taskList = chronosService.getAllTasks()
        taskList.forEach {chronosService.deleteTask(it)}

        Intents.release()


    }

    @Test
    fun testViews() {
        dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()
        modified_id = chronosService.addOrUpdateTask(modified_task).taskId.toInt()

        onView(withId(R.id.srl_ma)).perform(swipeDown())

        onView(withId(R.id.rv_ma))
                .check(matches(atPosition(0, hasDescendant(withText(dummyTask.title)))))

        onView(withId(R.id.rv_ma))
                .check(matches(atPosition(1, hasDescendant(withText(modified_task.title)))))

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

    @Test
    fun testDelete() = runBlocking {
        dummy_id = chronosService.addOrUpdateTask(dummyTask).taskId.toInt()
        modified_id = chronosService.addOrUpdateTask(modified_task).taskId.toInt()

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
        {"Task couldn't be deleted."}
    }
}