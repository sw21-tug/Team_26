package com.tugraz.chronos

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class OptionsActivity : AppCompatActivity() {

    enum class Language(val string: String, val locale: Locale = Locale(string)){
        ENGLISH("en"),
        RUSSIAN("ru")
    }

    lateinit var change_language: Button

    private fun setLocale(language: Language){
        Locale.setDefault(language.locale)
        val config = Configuration()
        config.locale = language.locale //TODO update to higher android version, change to config.setLocale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics) //TODO refactor
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", language.string)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences ( name: "Sett")

    }

    fun languageDialog(){
        val builder = AlertDialog.Builder(this);
        val languages = arrayOf(getString(R.string.english), getString(R.string.russian))
        builder.setTitle(R.string.change_language)
        builder.setSingleChoiceItems(languages, -1){ dialog, which ->
            if(which == 0){
                setLocale(Language.ENGLISH)
                recreate()
            } else if(which == 1){
                setLocale(Language.RUSSIAN)
                recreate()
            }
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        change_language = findViewById(R.id.change_language)
        change_language.setOnClickListener {

        }
    }
}