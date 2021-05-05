package com.tugraz.chronos.model.dao

import androidx.room.*
import com.tugraz.chronos.model.entities.Task

@Dao
interface TaskDAO {
    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task WHERE taskId = :id")
    suspend fun getTaskByID(id: Long): Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task): Void

    @Delete
    suspend fun deleteTask(task: Task): Void
}