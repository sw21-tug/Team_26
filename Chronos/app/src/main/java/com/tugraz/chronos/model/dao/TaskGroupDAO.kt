package com.tugraz.chronos.model.dao

import androidx.room.*
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.entities.TaskGroupPhoto
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

    @Query ("SELECT * FROM TaskGroupPhoto WHERE groupId = :groupId" )
    fun getAllPhotosFromGroup(groupId: Long): List<TaskGroupPhoto>

//--------------------------------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTaskGroupPhoto(taskGroupPhoto: TaskGroupPhoto): Long

    @Update
    fun updateTaskGroupPhoto(TaskGroupPhoto: TaskGroupPhoto)

    @Delete
    fun deleteTaskGroupPhoto(taskGroupPhoto: TaskGroupPhoto)

    @Query ("DELETE FROM TaskGroupPhoto WHERE toDelete = 1")
    fun deleteToDeletePhotos()

    @Query ("SELECT * FROM TaskGroupPhoto WHERE toDelete = 1")
    fun getToDeletePhotos(): List<TaskGroupPhoto>

    @Query ("UPDATE TaskGroupPhoto SET toDelete = 0 WHERE toDelete = 1")
    fun cancelPendingDeletes()
}