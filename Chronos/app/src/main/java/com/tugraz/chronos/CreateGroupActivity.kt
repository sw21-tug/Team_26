package com.tugraz.chronos

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

class CreateGroupActivity : AppCompatActivity(),  View.OnClickListener {

    lateinit var btn_create: Button
    lateinit var et_group_name: EditText
    var db_wrapper = DBWrapper()
    lateinit var coordinator: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        btn_create = findViewById(R.id.btn_create_group)
        btn_create.setOnClickListener(this)
        et_group_name = findViewById(R.id.group_name)
        coordinator = findViewById(R.id.cl_ct)
    }

    override fun onClick(v: View?) {
        if (et_group_name.text.toString().isEmpty())
        {
           Snackbar.make(
                coordinator,
                R.string.err_title_empty,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val db_success = db_wrapper.addTaskGroup(et_group_name.text.toString())

        if (db_success == -1)
        {
            Snackbar.make(
                coordinator,
                R.string.err_db_insert,
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        else
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

}