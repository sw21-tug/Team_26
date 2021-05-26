package com.tugraz.chronos.model.service

import android.content.Context
import com.tugraz.chronos.model.database.ChronosDB
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.entities.TaskGroupRelation
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.util.*

class ChronosService(private val context: Context) {
    private var db: ChronosDB? = null

    fun getAllTasks(): List<Task> {
        db = ChronosDB.getChronosDB(context)

        return runBlocking { db!!.taskDao().getAllTasks() }
    }

    fun addTask(group_id: Long, title: String, description: String, date: LocalDateTime): Task {
        db = ChronosDB.getChronosDB(context)

        val task = Task(group_id, title, description, date.toString())
        return runBlocking {
            val taskID = db!!.taskDao().insertTask(task)
            db!!.taskDao().getTaskByID(taskID)
        }
    }

    fun addOrUpdateTask(task: Task, groupID: Long?=null, title: String?=null, description: String?=null, date: LocalDateTime?=null): Task {
        db = ChronosDB.getChronosDB(context)

        val updatedTask: Task

        if (task.taskId == 0L) {
            updatedTask = runBlocking {
                val taskID = db!!.taskDao().insertTask(task)
                db!!.taskDao().getTaskByID(taskID)
            }
        }
        else {
            if (groupID != null) {
                val dbTaskGroup = runBlocking { db!!.taskGroupDao().getGroupByID(groupID) }
                task.groupId = if (dbTaskGroup != null) groupID else 0L
            }
            if (title != null) {  task.title = title }
            if (description != null) { task.description = description }
            if (date != null) { task.date = date.toString() }
            updatedTask = runBlocking {
                db!!.taskDao().updateTask(task)
                db!!.taskDao().getTaskByID(task.taskId)
            }
        }
        return updatedTask
    }

    fun getTaskById(taskID: Long): Task {
        db = ChronosDB.getChronosDB(context)

        return runBlocking { db!!.taskDao().getTaskByID(taskID) }
    }

    fun deleteTask(task: Task) {
        db = ChronosDB.getChronosDB(context)

        if (task.groupId != 0L) {
            task.groupId = 0L
            runBlocking { db!!.taskDao().updateTask(task) }
        }

        runBlocking { db!!.taskDao().deleteTask(task) }
    }

    //----------------------------------------------------------------------------------------------

    fun getAllGroups(): List<TaskGroupRelation> {
        db = ChronosDB.getChronosDB(context)

        return runBlocking {
            db!!.taskGroupDao().getAllGroups()
        }
    }

    fun addTaskGroup(title: String, complete: Boolean?=false, colour: String?="#ffffff"): TaskGroupRelation {
        db = ChronosDB.getChronosDB(context)

        val taskGroup = TaskGroup(title, colour!!)
        return runBlocking{
            val taskGroupID = db!!.taskGroupDao().insertGroup(taskGroup)
            db!!.taskGroupDao().getGroupByID(taskGroupID)
        }
    }

    fun addOrUpdateTaskGroup(taskGroup: TaskGroup, title: String?=null): TaskGroupRelation {
        db = ChronosDB.getChronosDB(context)

        val updatedTaskGroup: TaskGroupRelation

        if (taskGroup.taskGroupId == 0L) {
            updatedTaskGroup = runBlocking {
                val taskGroupID = db!!.taskGroupDao().insertGroup(taskGroup)
                db!!.taskGroupDao().getGroupByID(taskGroupID)
            }
        }
        else {
            if (title != null) { taskGroup.title = title }
            updatedTaskGroup = runBlocking {
                db!!.taskGroupDao().updateGroup(taskGroup)
                db!!.taskGroupDao().getGroupByID(taskGroup.taskGroupId)
            }
        }
        return updatedTaskGroup
    }

    fun getTaskGroupById(taskGroupID: Long): TaskGroupRelation {
        db = ChronosDB.getChronosDB(context)

        return runBlocking { db!!.taskGroupDao().getGroupByID(taskGroupID) }
    }

    fun deleteTaskGroup(taskGroup: TaskGroup) {
        db = ChronosDB.getChronosDB(context)

        val dbTaskGroup = runBlocking {
            db!!.taskGroupDao().getGroupByID(taskGroup.taskGroupId) }
        runBlocking {
            db!!.taskGroupDao().deleteGroup(taskGroup)
            dbTaskGroup.taskList.forEach {
                it.groupId = 0L
                db!!.taskDao().updateTask(it)
            }
        }
    }

    fun deleteGroupWithAllTasks(taskGroup: TaskGroup) {
        db = ChronosDB.getChronosDB(context)

        val dbTaskGroup = runBlocking { db!!.taskGroupDao().getGroupByID(taskGroup.taskGroupId) }
        runBlocking {
            dbTaskGroup.taskList.forEach {
                it.groupId = 0L
                db!!.taskDao().deleteTask(it)
            }
        }

        runBlocking { db!!.taskGroupDao().deleteGroup(taskGroup) }
    }
}