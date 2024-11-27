package com.example.inzyn

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
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

        binding.allstats.setOnClickListener {
            showStatisticsDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun showStatisticsDialog() {
        println("111111111")
        lifecycleScope.launchWhenStarted {
            println("222222222")
            val totalSets = calculateTotalSets()
            val totalVolume = calculateTotalVolume()
            val averageVolume = calculateAverageVolume()
            val favoriteExercise = calculateFavoriteExercise()

            val statisticsMessage = """
                Łączna ilość wykonanych serii: $totalSets
                Łączna objętość treningowa: $totalVolume kg
                Średnia objętość na ćwiczenie: $averageVolume kg
                Ulubione ćwiczenie: $favoriteExercise
            """.trimIndent()

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Statystyki ćwiczeń")
                    .setMessage(statisticsMessage)
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    private suspend fun calculateTotalSets(): Int = withContext(Dispatchers.IO) {
        val allSets = RepositoryLocator.setRepository.getSetList()
        allSets.size
    }

    private suspend fun calculateTotalVolume(): Double = withContext(Dispatchers.IO) {
        val allSets = RepositoryLocator.setRepository.getSetList()
        allSets.sumOf { it.weight * it.reps }
    }

    private suspend fun calculateAverageVolume(): Number = withContext(Dispatchers.IO) {
        val allSets = RepositoryLocator.setRepository.getSetList()
        if (allSets.isNotEmpty()) {
            val average = allSets.sumOf { it.weight * it.reps } / allSets.size
            String.format("%.2f", average).toDouble() // Zaokrąglenie do dwóch miejsc po przecinku i konwersja do Double
        } else {
            0.0
        }
    }

    private suspend fun calculateFavoriteExercise(): String = withContext(Dispatchers.IO) {
        val allSets = RepositoryLocator.setRepository.getSetList()
        val exerciseCounts = allSets.groupingBy { it.exerciseID }.eachCount()
        val favoriteExerciseId = exerciseCounts.maxByOrNull { it.value }?.key

        if (favoriteExerciseId != null) {
            val exercise = RepositoryLocator.exerciseRepository.getExerciseById(favoriteExerciseId)
            exercise?.name ?: "Ćwiczenie usunięte z bazy danych"
        } else {
            "Brak danych"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
