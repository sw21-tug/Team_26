package com.tugraz.chronos

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton

import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tugraz.chronos.model.service.ChronosService

lateinit var chronosService: ChronosService

class MainActivity : AppCompatActivity() {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout = findViewById(R.id.srl_ma)
        swipeRefreshLayout.setOnRefreshListener {
            loadTasks()
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 500)
        }

        chronosService = ChronosService(this)
        loadTasks()
        val fab = findViewById<FloatingActionButton>(R.id.btn_ma_add)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
            finish()
        }
    }

    fun loadTasks() {
        val task_list = chronosService.getAllTasks()
        val linearLayout : LinearLayout = findViewById(R.id.ll_ma_sv)
        linearLayout.removeAllViews()
        var item_counter = 0
        for (item in task_list) {

            val button = Button(this)
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 220)
            button.id = item.taskId.toInt()

            val title = item.title
            val description = item.description
            val date = item.date
            val space = "    "
            val text = title + space + description + "\n" + date

            button.text = text
            button.setTextColor(Color.BLACK)
            button.textSize = 15F

            if (item_counter % 2 == 0) {
                button.setBackgroundColor(Color.LTGRAY)
            } else {
                button.setBackgroundColor(Color.GRAY)
            }

            item_counter += 1
            linearLayout.addView(button)
        }
    }
}