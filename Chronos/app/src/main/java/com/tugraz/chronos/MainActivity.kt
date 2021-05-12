package com.tugraz.chronos

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.service.ChronosService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

lateinit var chronosService: ChronosService

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavigationDrawer()

        chronosService = ChronosService(this)

        swipeRefreshLayout = findViewById(R.id.srl_ma)
        swipeRefreshLayout.setOnRefreshListener {
            loadTasks()
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 500)
        }

        loadTasks()

        // fab for CreateTaskActivity
        val fab = findViewById<FloatingActionButton>(R.id.btn_ma_add)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
            finish()
        }
    }

    fun sortTasks(task_list: List<Task>): List<Task> {
        return task_list.sortedBy { value -> value.date }
    }

    fun loadTasks() {
        val task_list = sortTasks(chronosService.getAllTasks())
        val linearLayout: LinearLayout = findViewById(R.id.ll_ma_sv)
        linearLayout.removeAllViews()
        var item_counter = 0
        for (item in task_list) {

            val button = Button(this)
            button.layoutParams =
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 220)
            button.id = item.taskId.toInt()

            // button click logic
            val intent = Intent(this, TaskDetailsActivity::class.java)
            val b = Bundle()
            b.putInt("id", button.id)
            intent.putExtras(b) // Put id to intent

            button.setOnClickListener {
                startActivity(intent)
                finish()
            }

            val title = item.title
            //val description = item.description
            val date1 = LocalDateTime.parse(
                item.date,
                DateTimeFormatter.ISO_DATE_TIME
            )
            val date2 = LocalDateTime.now()

            val input: Long = date2.until(date1, ChronoUnit.SECONDS)

            val days = (input / 86400).toString()
            val hours = ((input % 86400) / 3600).toString()
            val minutes = (((input % 86400) % 3600) / 60).toString()
            val seconds = (((input % 86400) % 3600) % 60).toString()

            val space = "                 "
            val timeUntil = days + "d " + hours + ":" + minutes + ":" + seconds
            val text = title + space + timeUntil

            button.text = text
            button.setTextColor(Color.BLACK)
            button.textSize = 20F

            if (item_counter % 2 == 0) {
                button.setBackgroundColor(Color.LTGRAY)
            } else {
                button.setBackgroundColor(Color.GRAY)
            }

            item_counter += 1
            linearLayout.addView(button)
        }
    }

    private fun initNavigationDrawer() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


    }

    // used for create Group Button in drawer menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.createGroup -> {
                startActivity(Intent(this, CreateGroupActivity::class.java))
                finish()
            }
            R.id.options_button -> {
                startActivity(Intent(this, OptionsActivity::class.java))
                finish()
            }
        }
        return true
    }

    // used for closing the drawer menu
    override fun onBackPressed() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}