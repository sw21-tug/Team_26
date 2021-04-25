package com.tugraz.chronos.model.dao

import androidx.room.*
import com.tugraz.chronos.model.entities.TaskGroup

@Dao
interface TaskGroupDAO {
    @Query("SELECT * FROM TaskGroup")
    fun getAllGroups(): List<TaskGroup>

    @Insert
    fun insertGroup(group: TaskGroup): Long

    @Update
    fun updateGrup(group: TaskGroup)

    @Delete
    fun deleteGroup(group: TaskGroup)
}