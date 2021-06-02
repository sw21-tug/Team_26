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

    @Transaction
    @Query ("SELECT * FROM TaskGroupPhoto WHERE groupId = :groupId" )
    suspend fun getAllPhotosFromGroup(groupId: Long): List<TaskGroupPhoto>

//--------------------------------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskGroupPhoto(taskGroupPhoto: TaskGroupPhoto): Long

    @Update
    suspend fun updateTaskGroupPhoto(TaskGroupPhoto: TaskGroupPhoto)

    @Delete
    suspend fun deleteTaskGroupPhoto(taskGroupPhoto: TaskGroupPhoto)

    @Transaction
    @Query ("DELETE FROM TaskGroupPhoto WHERE toDelete = 1")
    suspend fun deleteToDeletePhotos()

    @Transaction
    @Query ("SELECT * FROM TaskGroupPhoto WHERE toDelete = 1")
    suspend fun getToDeletePhotos(): List<TaskGroupPhoto>

    @Transaction
    @Query ("UPDATE TaskGroupPhoto SET toDelete = 0 WHERE toDelete = 1")
    suspend fun cancelPendingDeletes()
}