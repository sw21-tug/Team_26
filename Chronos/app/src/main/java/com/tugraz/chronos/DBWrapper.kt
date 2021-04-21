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

    fun addTask(id: Int, title: String, description: String, date: Date): Int {
        task_list.put(id, Task(title, description, date))
        return id
    }

    fun modifyTask(id: Int, title: String, description: String, date: Date): Int {
        if(task_list.containsKey(id)){
            task_list[id] = Task(title, description, date)
            return id
        }
        return -1
    }

    fun deleteTask(id: Int): Int {
        if(task_list.containsKey(id)){
            task_list.remove(id)
            return id;
        }
        return -1
    }

    fun addTaskGroup(id: Int, title: String): Int {
        task_group_list.put(id, TaskGroup(title, mutableListOf()))
        return id
    }

    fun modifyTaskGroup(id: Int, title: String): Int {
        if(task_group_list.containsKey(id)){
            var taskGroup : TaskGroup? = task_group_list[id]
            taskGroup?.let {
                taskGroup.title = title
                task_group_list[id] = taskGroup
                return id
            }
        }
        return -1
    }

    fun deleteTaskGroup(id: Int): Int {
        if(task_group_list.containsKey(id)){
            task_group_list.remove(id)
            return id
        }
        return -1
    }

    fun getTasks(): MutableMap<Int, Task> {
        return task_list
    }

    fun getTaskGroups(): MutableMap<Int, TaskGroup> {
        return task_group_list
    }

    fun getTaskFromTaskGroup(id: Int): MutableList<Int> {
        if(task_group_list.containsKey(id)){
            var taskGroup : TaskGroup? = task_group_list[id]
            taskGroup?.let { return taskGroup.tasks }
        }
        return mutableListOf()
    }

    fun addTaskToTaskGroup(group_id: Int, task_id: Int): Int {
        if(task_group_list.containsKey(group_id)){
            if(task_list.containsKey(task_id)){
                var taskGroup : TaskGroup? = task_group_list[group_id]
                taskGroup?.let {
                    taskGroup.tasks.add(task_id)
                    task_group_list[group_id] = taskGroup
                    return group_id
                }
            }
        }
        return -1
    }
}