package com.tugraz.chronos.model.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskGroupRelation (
    @Embedded val taskGroup: TaskGroup,
    @Relation(
        parentColumn = "taskGroupId",
        entityColumn = "groupId"
    )
    val taskList: List<Task>,

    @Relation(
        parentColumn = "taskGroupId",
        entityColumn = "groupId"
    )
    val photoList: List<TaskGroupPhoto>
)