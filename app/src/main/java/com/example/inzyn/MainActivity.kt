package com.example.inzyn

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.inzyn.databinding.ActivityMainBinding
import java.io.File
import java.time.LocalDateTime
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {

            R.id.language_settings -> {
                languageMenu()
                true
            }


            R.id.reset_database -> {
                resetDatabase("gym")
                true
            }

            R.id.change_theme -> {
                themeMenu()
                true
            }

            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun themeMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.toolbar), Gravity.RIGHT)
        popupMenu.menuInflater.inflate(R.menu.theme_popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { popupItem ->
            when (popupItem.itemId) {
                R.id.Dark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    true
                }

                R.id.White -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    true
                }

                else -> false
            }

        }
        popupMenu.show()
    }

    @SuppressLint("RtlHardcoded")
    private fun languageMenu() {
        val popupMenu = PopupMenu(this, findViewById(R.id.toolbar), Gravity.RIGHT)
        popupMenu.menuInflater.inflate(R.menu.language_popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { popupItem ->
            when (popupItem.itemId) {
                R.id.Polish -> {
                    changeLanguage("pl")
                    true
                }

                R.id.German -> {
                    changeLanguage("de")
                    true
                }

                R.id.Spanish -> {
                    changeLanguage("es")
                    true
                }

                R.id.English -> {
                    changeLanguage("en")
                    true
                }

                else -> false
            }

        }
        popupMenu.show()
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        recreate()

        Toast.makeText(this, "Language changed to ${locale.displayName}", Toast.LENGTH_SHORT).show()

    }

    private fun resetDatabase(dbName: String) {
        val dbFile = File(applicationContext.getDatabasePath(dbName).absolutePath)
        if (dbFile.exists()) {
            dbFile.delete()
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}