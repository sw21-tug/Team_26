package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
        var groupId: Long,
        var title: String,
        var description: String,
        var date: String,
        var complete: Boolean
)
{
        constructor(_groupId: Long, _title: String, _description: String, _date: String, _complete: Boolean?=false): this(0, 0L, "", "", "", _complete!!) {
                groupId = _groupId
                title = _title
                this.description = _description
                this.date = _date
                this.complete = _complete
        }
}