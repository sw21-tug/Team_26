package com.tugraz.chronos

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class OptionsActivity : AppCompatActivity() {

    lateinit var change_language: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        setContentView(R.layout.activity_options)

        val actionBar = supportActionBar
        actionBar!!.title = resources.getString(R.string.app_name)

        change_language = findViewById(R.id.change_language)
        change_language.setOnClickListener {
          languageDialog()
        }
    }


    private fun setLocale(lang: String){
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale //TODO update to higher android version, change to config.setLocale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics) //TODO refactor
        val editor = getSharedPreferences("Options", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
    }

    private fun loadLocale() {
        val sharedPreferences = getSharedPreferences( "Options", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString ( "My_Lang", "")
        if (language != null) {
            setLocale(language)
        }
    }

    fun languageDialog(){
        val builder = AlertDialog.Builder(this);
        val languages = arrayOf(getString(R.string.english), getString(R.string.russian))
        builder.setTitle(R.string.change_language)
        builder.setSingleChoiceItems(languages, -1){ dialog, which ->
            if(which == 0){
                setLocale("en")
                recreate()
            } else if(which == 1){
                setLocale("ru")
                recreate()
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}