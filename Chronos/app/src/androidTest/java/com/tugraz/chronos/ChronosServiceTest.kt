package com.tugraz.chronos

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import com.tugraz.chronos.model.database.ChronosDB
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.entities.TaskGroup
import com.tugraz.chronos.model.entities.TaskGroupPhoto
import com.tugraz.chronos.model.entities.TaskGroupRelation
import com.tugraz.chronos.model.service.ChronosService
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.util.*

@RunWith(AndroidJUnit4::class)
class ChronosServiceTest {
    private var db: ChronosDB = ChronosDB.getTestDB(ApplicationProvider.getApplicationContext())!!
    private var chronosService = ChronosService(ApplicationProvider.getApplicationContext())

    private lateinit var groupList: List<TaskGroupRelation>
    private lateinit var taskList: List<Task>
    private lateinit var photoList: MutableList<List<TaskGroupPhoto>>

    @Before
    fun setup() {
        val tempGroupList = listOf(
                TaskGroup("This is a Test Group"),
                TaskGroup("This is another Test Group", "#000000"))

        val tempTaskList = listOf(
                Task(0, "This is a Test Task", "Description", ""),
                Task(0, "This is another Test Task", "Another Description", "", true))

        insertEntries(tempGroupList, tempTaskList)
    }

    @After
    fun teardown() = runBlocking {
        val taskList = db.taskDao().getAllTasks()
        val groupList = db.taskGroupDao().getAllGroups()

        for (groupEntry in groupList) {
            for (taskEntry in groupEntry.taskList) {
                taskEntry.groupId = 0
                db.taskDao().updateTask(taskEntry)
            }

            for (photoEntry in groupEntry.photoList) {
                db.taskGroupDao().deleteTaskGroupPhoto(photoEntry)
            }
            db.taskGroupDao().deleteGroup(groupEntry.taskGroup)
        }

        taskList.forEach { db.taskDao().deleteTask(it) }
    }

    fun insertEntries(tempGroupList: List<TaskGroup>, tempTaskList: List<Task>) = runBlocking {
        tempGroupList.forEach { db.taskGroupDao().insertGroup(it) }
        tempTaskList.forEach { db.taskDao().insertTask(it) }

        groupList = db.taskGroupDao().getAllGroups()
        taskList = db.taskDao().getAllTasks()

        val lastTask = taskList[taskList.lastIndex]
        lastTask.groupId = groupList[groupList.lastIndex].taskGroup.taskGroupId
        db.taskDao().updateTask(lastTask)

        val tempPhotoList = listOf(
                TaskGroupPhoto(groupList[groupList.lastIndex].taskGroup.taskGroupId, false),
                TaskGroupPhoto(groupList[groupList.lastIndex].taskGroup.taskGroupId, false)
        )
        tempPhotoList.forEach { db.taskGroupDao().insertTaskGroupPhoto(it) }

        groupList = db.taskGroupDao().getAllGroups()
        taskList = db.taskDao().getAllTasks()
        for (group in groupList) {
            photoList.add(group.photoList)
        }
    }

    @Test
    fun testGetAllTasksFromService() {
        val tasks = chronosService.getAllTasks()

        assert(tasks.size == taskList.size) { "Could not retrieve the right amount of tasks in ChronosService." }
    }

    @Test
    fun testAddTaskFromService() {
        val dbTask = chronosService.addTask(0, "This is a service test", "This is a Service Test Description", LocalDateTime.parse("-999999999-01-01T00:00:00"))

        assert(dbTask.taskId != 0L) { "Creating and inserting a new Task via ChronosService went wrong" }
    }

    @Test
    fun testGetTaskByIdFromService() {
        val taskToCheck = taskList[taskList.lastIndex]

        val dbTask = chronosService.getTaskById(taskToCheck.taskId)

        assert(taskToCheck == dbTask) { "Searching Task by TaskID via ChronosService went wrong." }
    }

    @Test
    fun testUpdateTaskWithNonexistingGroupFromService() {
        val newGroupID = -1L
        val newTitle = "This is a changed Service Task Text Title"
        val newDescription = "This is a changed Service Description Text"
        val newDate = LocalDateTime.parse("-999999999-01-01T00:00:00")
        var complete = false
        newDate.plusMinutes(3600)
        val updateTask = taskList[taskList.lastIndex]
        val groupID = updateTask.groupId
        val sizeBefore = groupList[groupList.lastIndex].taskList.size

        complete = !updateTask.complete
        chronosService.addOrUpdateTask(updateTask, groupID = newGroupID, title = newTitle, description = newDescription, date = newDate, complete = complete)
        assert(updateTask.groupId == 0L) { "Updating GroupID from Task via ChronosService went wrong." }
        assert(updateTask.title == newTitle) { "Updating Title from Task via ChronosService went wrong." }
        assert(updateTask.description == newDescription) { "Updating Description from Task via ChronosService went wrong." }
        assert(updateTask.date == newDate.toString()) { "Updating Date from Task via ChronosService went wrong." }
        assert(updateTask.complete == complete) {"Updating Complete from Task via ChronosService went wrong."}
        assert(sizeBefore - 1 == runBlocking { db.taskGroupDao().getGroupByID(groupID) }.taskList.size) { "Updating Group's Task via ChronosService went wrong." }
    }

    @Test
    fun testUpdateTaskWithExistingGroupFromService() {
        val newGroupID = groupList[groupList.lastIndex - 1].taskGroup.taskGroupId
        val newTitle = "This is a changed Service Task Text Title"
        val newDescription = "This is a changed Service Description Text"
        val newDate = LocalDateTime.parse("-999999999-01-01T00:00:00")
        newDate.plusMinutes(3600)
        var complete = false
        val updateTask = taskList[taskList.lastIndex]
        val groupID = updateTask.groupId
        val sizeBefore = groupList[groupList.lastIndex].taskList.size
        val changedSizedBefore = groupList[groupList.lastIndex - 1].taskList.size

        complete = !updateTask.complete
        chronosService.addOrUpdateTask(updateTask, groupID = newGroupID, title = newTitle, description = newDescription, date = newDate)
        assert(updateTask.groupId == newGroupID) { "Updating GroupID from Task via ChronosService went wrong." }
        assert(updateTask.title == newTitle) { "Updating Title from Task via ChronosService went wrong." }
        assert(updateTask.description == newDescription) { "Updating Description from Task via ChronosService went wrong." }
        assert(updateTask.date == newDate.toString()) { "Updating Date from Task via ChronosService went wrong." }
        assert(updateTask.complete == complete) {"Updating Complete from Task via ChronosService went wrong."}
        assert(sizeBefore - 1 == runBlocking { db.taskGroupDao().getGroupByID(groupID) }.taskList.size) { "Updating Group's Task (Retracting old Group) via ChronosService went wrong." }
        assert(changedSizedBefore + 1 == runBlocking { db.taskGroupDao().getGroupByID(newGroupID) }.taskList.size) { "Updating Group's Task (Assigning new Group) via ChronosService went wrong." }
    }

    @Test
    fun testDeleteTaskFromService() {
        val groupTaskSizeBefore = groupList[groupList.lastIndex].taskList.size
        val taskToDelete = taskList[taskList.lastIndex]
        val groupIdToCheck = taskToDelete.groupId

        chronosService.deleteTask(taskToDelete)

        assert(taskList.size - 1 == runBlocking { db.taskDao().getAllTasks() }.size) { "Deleting Task via ChronosService went wrong." }
        assert(groupTaskSizeBefore - 1 == runBlocking { db.taskGroupDao().getGroupByID(groupIdToCheck) }.taskList.size) { "Updating Group's Task (Deleting Task) via ChronosService went wrong" }
    }

    @Test
    fun testGetAllTaskGroupsFromService() {
        val groups = chronosService.getAllGroups()

        assert(groups.size == groupList.size) { "Could not retrieve the right amount of groups in ChronosService." }
    }

    @Test
    fun testAddTaskGroupFromService() {
        val dbTaskGroup = chronosService.addTaskGroup("This is a service test")

        assert(dbTaskGroup.taskGroup.taskGroupId != 0L) { "Creating and inserting a new TaskGroup via ChronosService went wrong" }
        assert(dbTaskGroup.taskGroup.colour == "#ffffff") { "Creating and inserting a new TaskGroup(colour) via ChronosService went wrong" }
    }

    @Test
    fun testGetTaskGroupByIdFromService() {
        val taskGroupToCheck = groupList[groupList.lastIndex]

        val dbTaskGroup = chronosService.getTaskGroupById(taskGroupToCheck.taskGroup.taskGroupId)

        assert(taskGroupToCheck.taskGroup.taskGroupId == dbTaskGroup.taskGroup.taskGroupId) { "Searching Group (taskGroup ID) by GroupID via ChronosService went wrong." }
        assert(taskGroupToCheck.taskGroup.title == dbTaskGroup.taskGroup.title) { "Searching Group (taskGroup title) by GroupID via ChronosService went wrong." }
        assert(taskGroupToCheck.taskList.size == dbTaskGroup.taskList.size) { "Searching Group (taskList size) by GroupID via ChronosService went wrong." }
        assert(taskGroupToCheck.photoList.size == dbTaskGroup.photoList.size) { "Searching Group (photoList size) by GroupID via ChronosService went wrong." }
    }

    @Test
    fun testUpdateTaskGroupFromService() {
        val newTitle = "This is a changed Service TaskGroup Text Title"
        val newColour = "#000000"
        val updateTaskGroup = groupList[groupList.lastIndex]

        chronosService.addOrUpdateTaskGroup(updateTaskGroup.taskGroup, title=newTitle, colour=newColour)
        assert(updateTaskGroup.taskGroup.title == newTitle) {"Updating Title from Group via ChronosService went wrong."}
        assert(updateTaskGroup.taskGroup.colour == newColour) {"Updating Colour from Group via ChronosService went wrong."}
    }

    @Test
    fun testDeleteTaskGroupWithoutTasksFromService() {
        val taskGroupToDelete = groupList[groupList.lastIndex - 1]

        chronosService.deleteTaskGroup(taskGroupToDelete.taskGroup)

        assert(groupList.size - 1 == runBlocking { db.taskGroupDao().getAllGroups() }.size) { "Deleting Group via ChronosService went wrong." }
    }

    @Test
    fun testDeleteTaskGroupResetingTasksFromService() {
        val taskGroupToDelete = groupList[groupList.lastIndex]

        chronosService.deleteTaskGroup(taskGroupToDelete.taskGroup)

        assert(groupList.size - 1 == runBlocking { db.taskGroupDao().getAllGroups() }.size) { "Deleting Group via ChronosService went wrong." }
        assert(runBlocking { db.taskDao().getTaskByID(taskList[taskList.lastIndex].taskId) }.groupId == 0L) { "Deleting Group - resetting assigned Task went wrong" }
    }

    @Test
    fun testDeleteTaskGroupWithTasksFromService() {
        val taskGroupToDelete = groupList[groupList.lastIndex]

        chronosService.deleteGroupWithAllTasks(taskGroupToDelete.taskGroup)

        assert(groupList.size - 1 == runBlocking { db.taskGroupDao().getAllGroups() }.size) { "Deleting Group - Group itself via ChronosService went wrong." }
        assert(taskList.size - 1 == runBlocking { db.taskDao().getAllTasks() }.size) { "Deleting Group - deleting associated tasks went wrong" }
    }

    @Test
    fun testGetPhotosFromGroupFromService() {
        val lastGroupId = groupList.lastIndex
        val lastGroup = groupList[lastGroupId]

        assert (lastGroup.photoList.size == photoList[lastGroupId].size) { "Could not retrieve the right amount of Photos from Group in ChronosService." }
    }
}