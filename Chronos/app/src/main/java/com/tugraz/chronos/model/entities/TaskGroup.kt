package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TaskGroup (
    @PrimaryKey(autoGenerate = true) val taskGroupID: Long,
    var title: String
)