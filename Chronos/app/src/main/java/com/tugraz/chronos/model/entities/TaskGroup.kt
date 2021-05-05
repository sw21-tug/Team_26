package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TaskGroup (
    @PrimaryKey(autoGenerate = true) val taskGroupId: Long = 0,
    var title: String
)
{
    constructor(_title: String): this(0L, "") {
        title = _title
    }
}