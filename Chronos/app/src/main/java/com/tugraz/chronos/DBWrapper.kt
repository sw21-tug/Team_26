package com.tugraz.chronos

import java.util.*

data class Task(
    var title: String,
    var description: String,
    var date: Date
)

data class TaskGroup(
    var title: String,
    var tasks: MutableList<Int>
)

class DBWrapper {

    var task_list : MutableMap<Int, Task> = mutableMapOf()
    var task_group_list : MutableMap<Int, TaskGroup> = mutableMapOf()

    fun getNewTaskIdFromDb(): Int {
        // TODO: DB return the new id for task + unittest
        return 0
    }

    fun getNewTaskGroupIdFromDb(): Int {
        // TODO: DB return the new id for task group + unittest
        return 0
    }

    fun loadDataFromDb() {
        // TODO: Load tasks and task groups from DB + unittest
    }

    fun saveDataToDb() {
        // TODO: Save tasks and task groups to DB + unittest
    }

    fun addTask(title: String, description: String, date: Date): Int {
        val id = getNewTaskIdFromDb()
        return addTask(id, title, description, date)
    }

    fun addTask(id: Int, title: String, description: String, date: Date): Int {
        loadDataFromDb()
        if (task_list.containsKey(id)) {
            return -1
        }

        task_list.put(id, Task(title, description, date))
        saveDataToDb()
        return id
    }

    fun modifyTask(id: Int, title: String, description: String, date: Date): Int {
        loadDataFromDb()
        if(!task_list.containsKey(id)) {
            return -1;
        }

        task_list[id] = Task(title, description, date)
        saveDataToDb()
        return id
    }

    fun deleteTask(id: Int): Int {
        loadDataFromDb()
        if(!task_list.containsKey(id)) {
            return -1;
        }

        task_list.remove(id)
        saveDataToDb()
        return id
    }

    fun addTaskGroup(title: String): Int {
        val id = getNewTaskGroupIdFromDb()
        return addTaskGroup(id, title)
    }

    fun addTaskGroup(id: Int, title: String): Int {
        loadDataFromDb()
        if (task_group_list.containsKey(id)) {
            return -1
        }

        task_group_list.put(id, TaskGroup(title, mutableListOf()))
        saveDataToDb()
        return id
    }

    fun modifyTaskGroup(id: Int, title: String): Int {
        loadDataFromDb()
        if (!task_group_list.containsKey(id)){
            return -1
        }

        task_group_list[id] = TaskGroup(title, task_group_list[id]!!.tasks)
        saveDataToDb()
        return id
    }

    fun deleteTaskGroup(id: Int): Int {
        loadDataFromDb()
        if (!task_group_list.containsKey(id)){
            return -1
        }

        task_group_list.remove(id)
        saveDataToDb()
        return id
    }

    fun getTasks(): MutableMap<Int, Task> {
        loadDataFromDb()
        return task_list
    }

    fun getTaskGroups(): MutableMap<Int, TaskGroup> {
        loadDataFromDb()
        return task_group_list
    }

    fun getTasksFromTaskGroup(id: Int): MutableList<Int> {
        loadDataFromDb()
        if (!task_group_list.containsKey(id)){
            return mutableListOf()
        }

        return task_group_list[id]!!.tasks
    }

    fun addTaskToTaskGroup(group_id: Int, task_id: Int): Int {
        loadDataFromDb()
        if (!task_group_list.containsKey(group_id) || !task_list.containsKey(task_id)) {
            return -1
        }

        task_group_list[group_id]!!.tasks.add(task_id)
        saveDataToDb()
        return group_id
    }

    fun removeTaskFromTaskGroup(group_id: Int, task_id: Int): Int {
        loadDataFromDb()
        if (!task_group_list.containsKey(group_id) ||
                !task_list.containsKey(task_id) ||
                !task_group_list[group_id]!!.tasks.contains(task_id)) {
            return -1
        }

        task_group_list[group_id]!!.tasks.remove(task_id)
        saveDataToDb()
        return group_id
    }
}