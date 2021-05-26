package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskGroupPhoto(
    @PrimaryKey(autoGenerate = true) val photoID: Long,
    val groupId: Long,
    var toDelete: Boolean
)
{
    constructor(_groupId: Long, _toDelete: Boolean): this (0L, _groupId, false) {
        toDelete = _toDelete
    }
}