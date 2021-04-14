package com.tugraz.chronos

import java.util.*

data class Task(
        val id: Int,
        var title: String,
        var description: String,
        var date: Date
)

data class TaskGroup(
        val id: Int,
        var title: String,
        var tasks: IntArray
)

class DBWrapper {

    var task_list : Array<Task> = arrayOf()
    var task_group_list : Array<TaskGroup> = arrayOf()

    fun addTask(id: Int, title: String, description: String, date: Date) {
        // TODO: Implement function
    }

    fun modifyTask(id: Int, title: String, description: String, date: Date) {
        // TODO: Implement function
    }

    fun deleteTask(id: Int) {
        // TODO: Implement function
    }

    fun addTaskGroup(id: Int, title: String) {
        // TODO: Implement function
    }

    fun modifyTaskGroup(id: Int, title: String) {
        // TODO: Implement function
    }

    fun deleteTaskGroup(id: Int) {
        // TODO: Implement function
    }

    fun getTasks() {
        // TODO: Implement function
    }

    fun getTaskGroups() {
        // TODO: Implement function
    }

    fun getTaskFromTaskGroup(id: Int) {
        // TODO: Implement function
    }

    fun addTaskToTaskGroup(group_id: Int, task_id: Int) {
        // TODO: Implement function
    }
}