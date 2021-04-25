package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val TaskID: Long,
    var taskGroupID: Long,
    var title: String,
    var description: String,
    var date: String
)