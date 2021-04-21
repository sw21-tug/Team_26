package com.tugraz.chronos

import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class DBWrapperTest {

    var dbWrapper: DBWrapper = DBWrapper()
    val dummyTask: Task = Task("TestTask", "TestDescirption", LocalDateTime.now())
    val modified_task: Task = Task("ModifiedTitle", "ModifiedDesc", LocalDateTime.MIN)
    val dummyTaskGroup: TaskGroup = TaskGroup("TestGroup", mutableListOf())
    val modified_group: TaskGroup = TaskGroup("ModifiedGroup", mutableListOf())

    @After
    fun teardown()
    {
        dbWrapper.task_group_list.clear()
        dbWrapper.task_list.clear()
    }

    @Test
    fun test_addTask() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)

        // Add task that doesn't exist, return id
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Try to add task that already exists, return error
        ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(-1, ret)
    }

    @Test
    fun test_modifyTask() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)

        // Add dummy task to be edited
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Modify the existing task, return id
        ret = dbWrapper.modifyTask(0, modified_task.title, modified_task.description, modified_task.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(modified_task, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Try to modify the non-existing task, return error
        ret = dbWrapper.modifyTask(15, modified_task.title, modified_task.description, modified_task.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(modified_task, dbWrapper.task_list[0])
        assertEquals(-1, ret)
    }

    @Test
    fun test_deleteTask() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)

        // Add dummy task to be deleted
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Delete the existing task, return id
        ret = dbWrapper.deleteTask(0)
        assertEquals(0, dbWrapper.task_list.size)
        assertEquals(0, ret)

        // Try to delete the non-existing task, return error
        ret = dbWrapper.deleteTask(0)
        assertEquals(0, dbWrapper.task_list.size)
        assertEquals(-1, ret)
    }

    @Test
    fun test_addTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_group_list.size)

        // Add task group that doesn't exist, return id
        var ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Try to add task group that already exists, return error
        ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)
    }

    @Test
    fun test_modifyTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_group_list.size)

        // Add task group that doesn't exist, return id
        var ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Modify the existing group, return id
        ret = dbWrapper.modifyTaskGroup(0, modified_group.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(modified_group, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Try to modify the non-existing group, return error
        ret = dbWrapper.modifyTaskGroup(15, modified_group.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(modified_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)
    }

    @Test
    fun test_deleteTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_group_list.size)

        // Add task group that doesn't exist, return id
        var ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Delete the existing group, return id
        ret = dbWrapper.deleteTaskGroup(0)
        assertEquals(0, dbWrapper.task_group_list.size)
        assertEquals(0, ret)

        // Try to delete the existing group, return error
        ret = dbWrapper.deleteTaskGroup(0)
        assertEquals(0, dbWrapper.task_group_list.size)
        assertEquals(-1, ret)
    }

    @Test
    fun test_addTaskToTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)
        assertEquals(0, dbWrapper.task_group_list.size)

        // Add task that doesn't exist, return id
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Add task group that doesn't exist, return id
        ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Add existing task to existing task group, return id
        ret = dbWrapper.addTaskToTaskGroup(0, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        val new_group = TaskGroup(dummyTaskGroup.title, mutableListOf(0))
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Add non-existing task to existing task group, return error
        ret = dbWrapper.addTaskToTaskGroup(0, 15)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

        // Add existing task to non-existing task group, return error
        ret = dbWrapper.addTaskToTaskGroup(15, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

        // Add non-existing task to non-existing task group, return error
        ret = dbWrapper.addTaskToTaskGroup(15, 15)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)
    }

    @Test
    fun test_getTasksFromTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)
        assertEquals(0, dbWrapper.task_group_list.size)

        // Try to get tasks, but everything empty
        var tasks_ret = dbWrapper.getTasksFromTaskGroup(0)
        assertEquals(tasks_ret, mutableListOf<Int>())

        // Add task that doesn't exist, return id
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Add task group that doesn't exist, return id
        ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Add existing task to existing task group, return id
        ret = dbWrapper.addTaskToTaskGroup(0, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        val new_group = TaskGroup(dummyTaskGroup.title, mutableListOf(0))
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Get valid list of tasks
        tasks_ret = dbWrapper.getTasksFromTaskGroup(0)
        assertEquals(tasks_ret, mutableListOf<Int>(0))
    }

    @Test
    fun test_removeTaskFromTaskGroup() {
        // Check that dbwrapper is empty
        assertEquals(0, dbWrapper.task_list.size)
        assertEquals(0, dbWrapper.task_group_list.size)

        // Add task that doesn't exist, return id
        var ret = dbWrapper.addTask(0, dummyTask.title, dummyTask.description, dummyTask.date)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(0, ret)

        // Add task group that doesn't exist, return id
        ret = dbWrapper.addTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Add existing task to existing task group, return id
        ret = dbWrapper.addTaskToTaskGroup(0, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        val new_group = TaskGroup(dummyTaskGroup.title, mutableListOf(0))
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(0, ret)

        // Remove non-existing task from non-existing task group, return error
        ret = dbWrapper.removeTaskFromTaskGroup(15, 15)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

        // Remove existing task from non-existing task group, return error
        ret = dbWrapper.removeTaskFromTaskGroup(15, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

        // Remove non-existing task from existing task group, return error
        ret = dbWrapper.removeTaskFromTaskGroup(0, 15)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(new_group, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

        // Remove existing task from existing task group, return group id
        ret = dbWrapper.removeTaskFromTaskGroup(0, 0)
        assertEquals(1, dbWrapper.task_list.size)
        assertEquals(dummyTask, dbWrapper.task_list[0])
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(0, ret)
    }
}