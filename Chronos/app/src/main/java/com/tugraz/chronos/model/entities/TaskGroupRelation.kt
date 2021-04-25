package com.tugraz.chronos.model.entities

import androidx.room.Embedded
import androidx.room.Relation

class TaskGroupRelation (
    @Embedded val taskGroup: TaskGroup,
    @Relation(
        parentColumn = "taskGroupID",
        entityColumn = "taskGroupID"
    )
    val taskList: List<Task>
)