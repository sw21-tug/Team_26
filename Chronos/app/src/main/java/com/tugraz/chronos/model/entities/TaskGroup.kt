package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TaskGroup(
    @PrimaryKey(autoGenerate = true) val taskGroupId: Long = 0,
    var title: String,
    var color: Int
)
{
    constructor(_title: String, _color: Int): this(0L, "", 0) {
        title = _title
        color = _color
    }
}