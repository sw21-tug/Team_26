package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskGroup (
    @PrimaryKey(autoGenerate = true) val taskGroupId: Long = 0,
    var title: String,
    var colour: String
)
{
    constructor(_title: String, _colour: String?="#ffffff"): this (0L, "", _colour!!) {
        title = _title
    }
}