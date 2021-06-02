package com.tugraz.chronos.model.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tugraz.chronos.model.dao.TaskDAO
import com.tugraz.chronos.model.dao.TaskGroupDAO
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.entities.TaskGroupPhoto
import com.tugraz.chronos.model.entities.TaskGroupRelation

@Database(entities = arrayOf(Task::class, TaskGroup::class, TaskGroupPhoto::class), version = 3, exportSchema = false)
abstract class ChronosDB : RoomDatabase() {

    abstract fun taskDao(): TaskDAO
    abstract fun taskGroupDao(): TaskGroupDAO

    companion object {
        var INSTANCE: ChronosDB? = null

        fun getChronosDB(context: Context): ChronosDB? {
            if (INSTANCE == null) {
                Log.d("DB", "Creating ChronosDB")
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    ChronosDB::class.java, "ChronosDB")
                    .fallbackToDestructiveMigration().build()
            }
            return INSTANCE
        }

        fun getTestDB(context: Context): ChronosDB? {
            if (INSTANCE == null) {
                Log.d("DB", "Creating ChronosTestDB")
                INSTANCE = Room.inMemoryDatabaseBuilder(context, ChronosDB::class.java).build()
            }
            return INSTANCE
        }

        fun resetDatabase() {
            INSTANCE = null
        }
    }
}