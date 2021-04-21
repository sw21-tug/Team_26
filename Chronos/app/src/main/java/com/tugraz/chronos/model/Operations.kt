package com.tugraz.chronos.model

import android.provider.BaseColumns

const val SQL_CREATE_TASK_TABLE =
    "CREATE TABLE ${ChronosContract.Tasks.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ChronosContract.Tasks.COLUMN_NAME_TITLE} TEXT," +
            "${ChronosContract.Tasks.COLUMN_NAME_DESCR} TEXT," +
            "${ChronosContract.Tasks.COLUMN_NAME_DATE} TEXT)"

const val SQL_CREATE_TASK_GROUPS_TABLE =
    "CREATE TABLE ${ChronosContract.TaskGroups.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${ChronosContract.TaskGroups.COLUMN_NAME_TITLE} TEXT," +
            "${ChronosContract.TaskGroups.COLUMN_NAME_TASKS} TEXT)"

const val SQL_DELETE_TASK_TABLE = "DROP TABLE IF EXISTS ${ChronosContract.Tasks.TABLE_NAME}"
const val SQL_DELETE_TASK_GROUPS_TABLE = "DROP TABLE IF EXISTS ${ChronosContract.TaskGroups.TABLE_NAME}"
