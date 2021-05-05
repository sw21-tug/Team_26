package com.tugraz.chronos.model.dao

import androidx.room.*
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.entities.TaskGroupRelation

@Dao
interface TaskGroupDAO{
    @Transaction
    @Query("SELECT * FROM TaskGroup")
    suspend fun getAllGroups(): List<TaskGroupRelation>

    @Transaction
    @Query("SELECT * FROM TaskGroup WHERE taskGroupId = :id")
    suspend fun getGroupByID(id: Long): TaskGroupRelation

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TaskGroup): Long

    @Update
    suspend fun updateGroup(group: TaskGroup): Void

    @Delete
    suspend fun deleteGroup(group: TaskGroup): Void
}