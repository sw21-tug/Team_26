package com.tugraz.chronos

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDateTime


import androidx.annotation.RequiresApi

var dbWrapper: DBWrapper = DBWrapper()

@RequiresApi(Build.VERSION_CODES.O)
val dummyTask: Task = Task("TestTask", "TestDescirption", LocalDateTime.now())

fun addDummys() {
    for (item in 0..20) {
        dbWrapper.addTask(item, dummyTask.title + item, dummyTask.description, dummyTask.date)
    }
}

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO remove dummys
        addDummys()

        val task_list: MutableMap<Int, Task> = dbWrapper.getTasks()

        val scrollView = ScrollView(this)
        val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        scrollView.layoutParams = layoutParams

        val linearLayout = LinearLayout(this)
        val linearParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.layoutParams = linearParams

        scrollView.addView(linearLayout)

        for (item in task_list) {

            val button = Button(this)
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 220)
            button.id = item.key

            val title = task_list[item.key]?.title
            val description = task_list[item.key]?.description
            val date = task_list[item.key]?.date?.toLocalDate()
            val space = "    "
            val text = title + space + description + "\n\n" + date

            button.text = text
            button.setTextColor(Color.BLACK)
            button.textSize = 15F

            if (item.key % 2 == 0) {
                button.setBackgroundColor(Color.LTGRAY)
            } else {
                button.setBackgroundColor(Color.GRAY)
            }

            linearLayout.addView(button)
        }


        val relativeLayout = findViewById<RelativeLayout>(R.id.parentRelative)
        relativeLayout.addView(scrollView)

        val fab = FloatingActionButton(this)
        fab.id = 0
        fab.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        val fabHolder = LinearLayout(this)
        fabHolder.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        fabHolder.gravity = Gravity.END or Gravity.BOTTOM


        fabHolder.addView(fab)
        relativeLayout.addView(fabHolder)
    }
}