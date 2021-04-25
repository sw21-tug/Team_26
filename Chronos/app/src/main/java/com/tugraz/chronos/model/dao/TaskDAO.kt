package com.tugraz.chronos.model.dao

import androidx.room.*
import com.tugraz.chronos.model.entities.Task

@Dao
interface TaskDAO {
    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Insert
    fun insertTask(task: Task): Long

    @Update
    fun updateTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}