package com.tugraz.chronos.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true) val taskId: Long = 0,
        var groupId: Long,
        var title: String,
        var description: String,
        var date: String
)
{
        constructor(_groupId: Long, _title: String, _description: String, _date: String): this(0, 0L, "", "", "") {
                groupId = _groupId
                title = _title
                this.description = _description
                this.date = _date
        }
}