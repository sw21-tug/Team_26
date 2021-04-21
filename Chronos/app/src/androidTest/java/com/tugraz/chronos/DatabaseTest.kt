package com.tugraz.chronos

import android.content.ContentValues
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tugraz.chronos.model.ChronosContract.Tasks
import com.tugraz.chronos.model.ChronosContract.TaskGroups
import com.tugraz.chronos.model.ChronosDBHelper
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private var database = Firebase.database
    private var context = InstrumentationRegistry.getInstrumentation().targetContext
    private var localDatabase = ChronosDBHelper(context)

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {

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
    fun insertDataIntoLocalDatabse() {
        val db = localDatabase.writableDatabase

        val values = ContentValues().apply {
            put(Tasks.COLUMN_NAME_TITLE, "Test Entry")
            put(Tasks.COLUMN_NAME_DATE, "" + Calendar.getInstance().time)
            put(Tasks.COLUMN_NAME_DESCR, "This is a Test Description")
        }

        val newRowId = db?.insert(Tasks.TABLE_NAME, null, values)
        assert(newRowId != (-1).toLong()) {"Returned an invalid ID (-1)"}
    }
}