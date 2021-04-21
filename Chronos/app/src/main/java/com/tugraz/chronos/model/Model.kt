package com.tugraz.chronos.model

import android.provider.BaseColumns

object ChronosContract {
    object Tasks : BaseColumns {
        const val TABLE_NAME = "task"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DESCR = "descr"
        const val COLUMN_NAME_DATE = "date"
    }

    object TaskGroups : BaseColumns {
        const val TABLE_NAME = "task_group"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_TASKS = "tasks"
    }
}
