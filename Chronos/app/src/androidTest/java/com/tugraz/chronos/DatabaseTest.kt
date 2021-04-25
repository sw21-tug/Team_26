package com.tugraz.chronos

import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tugraz.chronos.model.ChronosContract.Tasks
import com.tugraz.chronos.model.ChronosContract.TaskGroups
//import com.tugraz.chronos.model.ChronosContract.TaskGroupRelation
import com.tugraz.chronos.model.ChronosDBHelper
import com.tugraz.chronos.model.entities.TaskGroupRelation
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private var database = Firebase.database
    private var context = InstrumentationRegistry.getInstrumentation().targetContext
    private var writeableDb = ChronosDBHelper(context).writableDatabase
    private var readableDb = ChronosDBHelper(context).readableDatabase
    private var numOfTasks = 5;

    @Before
    fun setUp() {
        // Create a few tasks for testing
        val tasks = mutableListOf<Int>()

        for (elements in 1..numOfTasks) {
            val values = ContentValues().apply {
                put(Tasks.COLUMN_NAME_TITLE, "Test Entry $elements")
                put(Tasks.COLUMN_NAME_DATE, "" + Calendar.getInstance().time)
                put(Tasks.COLUMN_NAME_DESCR, "This is a Test Description for entry $elements")
            }

            val elementId = writeableDb?.insert(Tasks.TABLE_NAME, null, values)

            if (elementId != null) {
                tasks.add(elementId.toInt())
            }
        }

        // Create some task group for testing
        var values = ContentValues().apply {
            put(TaskGroups.COLUMN_NAME_TITLE, "Test Group1")
        }
        values = ContentValues().apply {
            put(TaskGroups.COLUMN_NAME_TITLE, "Test Group2")
        }

        for (elements in 1..numOfTasks) {
            if (elements %  2 == 0) {
                var values2 = ContentValues().apply {
//                    put(TaskGroupRelation.COLUMN_TASK_GROUP_ID, "")
                }
            }
        }
    }

    @After
    fun tearDown() {
        writeableDb.delete(Tasks.TABLE_NAME, null, null)
    }

    @Test
    fun onlineDatabaseConnection()  {
        val testString = "This is a test"
        val myRef = database.getReference("Connection Test")
        myRef.setValue(testString)

        database.reference.child("Connection Test").get().addOnSuccessListener {
            Log.i("success", "${it.value}")
            assert(testString == it.value)
        }.addOnFailureListener{
            Log.e("failure", "Error getting data", it)
            assert(false) {"Did not retrieve a result!"}
        }
    }

    @Test
    fun insertDataIntoLocalDatabase() {
        val values = ContentValues().apply {
            put(Tasks.COLUMN_NAME_TITLE, "Test Entry")
            put(Tasks.COLUMN_NAME_DATE, "" + Calendar.getInstance().time)
            put(Tasks.COLUMN_NAME_DESCR, "This is a Test Description")
        }

        val newRowId = writeableDb?.insert(Tasks.TABLE_NAME, null, values)
        assert(newRowId != (-1).toLong()) {"Returned an invalid ID (-1)"}
    }

//    @Test
//    fun readDataFromLocalDatabase() {
//        val selectCols = arrayOf(
//            BaseColumns._ID,
//            Tasks.COLUMN_NAME_TITLE,
//            Tasks.COLUMN_NAME_DESCR,
//            Tasks.COLUMN_NAME_DATE
//        )
//
//        val sortOrder = "${Tasks.COLUMN_NAME_DATE} DESC"
//
//        val cursor = readableDb.query(
//            Tasks.TABLE_NAME,
//            selectCols,
//            null,
//            null,
//            null,
//            null,
//            sortOrder
//        )
//
//        Assert.assertTrue(cursor.moveToNext())
//        Assert.assertEquals(numOfTasks, cursor.count)
//    }

    @Test
    fun deleteFromLocalDatabase() {
        val rowToDelete = 1
        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(rowToDelete.toString())

        val deletedRows = writeableDb.delete(Tasks.TABLE_NAME, selection, selectionArgs)
        Assert.assertEquals(rowToDelete, deletedRows)

        val selectCols = arrayOf(BaseColumns._ID)

        val cursor = readableDb.query(
            Tasks.TABLE_NAME,
            selectCols,
            null,
            null,
            null,
            null,
            null
        )

        Assert.assertTrue(cursor.moveToNext())
        Assert.assertEquals(numOfTasks - 1, cursor.count)
    }

    @Test
    fun updateLocalDatabase() {
        val rowToUpdate = 1
        val title = "I'm updated!"
        val values = ContentValues().apply {
            put(Tasks.COLUMN_NAME_TITLE, title)
        }

        val selection = "${BaseColumns._ID} LIKE ?"
        val selectionArgs = arrayOf(rowToUpdate.toString())
        val count = writeableDb.update(
            Tasks.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        Assert.assertEquals(1, count)

        val selectCols = arrayOf(BaseColumns._ID, Tasks.COLUMN_NAME_TITLE)

        val cursor = readableDb.query(
            Tasks.TABLE_NAME,
            selectCols,
            null,
            null,
            null,
            null,
            null
        )

        Assert.assertTrue(cursor.moveToNext())

        val idIndex = cursor.getColumnIndex(BaseColumns._ID)
        val titleIndex = cursor.getColumnIndex(Tasks.COLUMN_NAME_TITLE)

        while (cursor.getInt(idIndex) != rowToUpdate) {
            Assert.assertTrue(cursor.moveToNext())
        }

        Assert.assertEquals(title, cursor.getString(titleIndex))
    }

//    @Test
//    fun insertTaskGroupIntoLocalDatabase() {
//        val taskGroupValues = ContentValues().apply {
//            put(TaskGroups.COLUMN_NAME_TITLE, "Test Task Group")
//        }
//
//        val rowNum = writeableDb?.insert(TaskGroups.TABLE_NAME, null, taskGroupValues)
//
//        Assert.assertNotEquals(
//            (-1),
//            rowNum
//        )
//
//        for (taskId in 1..(numOfTasks / 2)) {
//            val values = ContentValues().apply {
//                put(TaskGroupRelation.TASK_GROUP_ID, rowNum)
//                put(TaskGroupRelation.TASK_ID, taskId)
//            }
//
//            Assert.assertNotEquals(
//                (-1),
//                writeableDb?.insert(TaskGroupRelation.TABLE_NAME, null, values)
//            )
//        }
//
//        writeableDb.delete(TaskGroups.TABLE_NAME, null, null)
//        writeableDb.delete(TaskGroupRelation.TABLE_NAME, null, null)
//    }
}