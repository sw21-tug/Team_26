package com.tugraz.chronos

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.tugraz.chronos.model.entities.Task
import com.tugraz.chronos.model.service.ChronosService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

lateinit var chronosService: ChronosService

class TaskItemHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.task_recycler_item, parent, false)) {
    private var mTitle: TextView? = null
    private var mDate: TextView? = null


    init {
        mTitle = itemView.findViewById(R.id.tv_tri_title)
        mDate = itemView.findViewById(R.id.tv_tri_date)
    }

    fun bind(task: Task) {
        val title = task.title
        val date1 = LocalDateTime.parse(
                task.date,
                DateTimeFormatter.ISO_DATE_TIME
        )
        val date2 = LocalDateTime.now()

        val input: Long = date2.until(date1, ChronoUnit.SECONDS)

        val days = input / 86400
        val hours = (input % 86400 ) / 3600
        val minutes = ((input % 86400 ) % 3600 ) / 60
        val seconds = ((input % 86400 ) % 3600 ) % 60
        val timeUntil = days.toString() + "d " + hours.toString() + ":" + minutes.toString() + ":" + seconds.toString()

        mTitle?.text = title
        mDate?.text = timeUntil
    }
}


class ListAdapter(private var list: List<Task>)
    : RecyclerView.Adapter<TaskItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaskItemHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: TaskItemHolder, position: Int) {
        val movie: Task = list[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = list.size
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var list_recycler_view: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initNavigationDrawer()

        chronosService = ChronosService(this)

        swipeRefreshLayout = findViewById(R.id.srl_ma)
        swipeRefreshLayout.setOnRefreshListener {
            val task_list = sortTasks(chronosService.getAllTasks())
            list_recycler_view.adapter = ListAdapter(task_list)
            Handler(Looper.getMainLooper()).postDelayed({
                swipeRefreshLayout.isRefreshing = false
            }, 500)
        }

        val task_list = sortTasks(chronosService.getAllTasks())
        list_recycler_view = findViewById(R.id.rv_ma)
        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = ListAdapter(task_list)
        }

        val fab = findViewById<FloatingActionButton>(R.id.btn_ma_add)
        fab.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
            finish()
        }
    }

    fun sortTasks(task_list: List<Task>): List<Task> {
        return task_list.sortedBy { value -> value.date }
    }

    private fun initNavigationDrawer() {
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


    }

    // used for create Group Button in drawer menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
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
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}