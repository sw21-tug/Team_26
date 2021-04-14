package com.tugraz.chronos

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import java.util.*

class DBWrapperTest {

    var dbWrapper: DBWrapper = DBWrapper()
    val dummyTask: Task = Task("TestTask", "TestDescirption", Date())
    val modified_task: Task = Task("ModifiedTitle", "ModifiedDesc", Date(1234))
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
        ret = dbWrapper.modifyTaskGroup(0, dummyTaskGroup.title)
        assertEquals(1, dbWrapper.task_group_list.size)
        assertEquals(dummyTaskGroup, dbWrapper.task_group_list[0])
        assertEquals(-1, ret)

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
    fun test_getTasks() {
        // TODO: Implement function
    }

    @Test
    fun test_getTaskGroups() {
        // TODO: Implement function
    }

    @Test
    fun test_getTaskFromTaskGroup() {
        // TODO: Implement function
    }

    @Test
    fun test_addTaskToTaskGroup() {
        // TODO: Implement function
    }

    @Test
    fun test_removeTaskFromTaskGroup() {
        // TODO: Implement function
    }
}