package com.tugraz.chronos

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import androidx.annotation.RequiresApi
import com.tugraz.chronos.model.service.ChronosService

lateinit var chronosService: ChronosService

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chronosService = ChronosService(this)

        val task_list = chronosService.getAllTasks()

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
            button.id = item.taskId.toInt()

            val title = item.title
            val description = item.description
            val date = item.date
            val space = "    "
            val text = title + space + description + "\n\n" + date

            button.text = text
            button.setTextColor(Color.BLACK)
            button.textSize = 15F

            //TODO: refactor - if task is deleted in database - id is not reused
            if (item.taskId.toInt() % 2 == 0) {
                button.setBackgroundColor(Color.LTGRAY)
            } else {
                button.setBackgroundColor(Color.GRAY)
            }

            linearLayout.addView(button)
        }


        val relativeLayout = findViewById<RelativeLayout>(R.id.parentRelative)
        relativeLayout.addView(scrollView)

        val fab = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
            finish()
        }
    }
}