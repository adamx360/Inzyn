package com.example.inzyn

import android.annotation.SuppressLint
import android.content.Intent
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
import com.example.inzyn.data.db.GymDb
import com.example.inzyn.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var gymDb: GymDb


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

        gymDb = GymDb()
//        gymDb.exerciseWrite()
//        gymDb.planWrite()
//        gymDb.writeSets()


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
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

            val messageTotalSets =
                String.format(getString(R.string.Total_series) + " " + totalSets)
            val messageTotalVolume =
                String.format(getString(R.string.total_training_volume) + " " + totalVolume + " " + "kg")
            val messageAvgVolume =
                String.format(getString(R.string.avg_exercise_volume) + " " + averageVolume + " " + "kg")
            val messageFavouriteExercise =
                String.format(getString(R.string.fav_exercise) + " " + favoriteExercise)

//            val statisticsMessage = """
//                Łączna ilość wykonanych serii: $totalSets
//                Łączna objętość treningowa: $totalVolume kg
//                Średnia objętość na ćwiczenie: $averageVolume kg
//                Ulubione ćwiczenie: $favoriteExercise
//            """.trimIndent()

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(String.format(getString(R.string.Exercise_stats)))
                    .setMessage(
                        messageTotalSets + "\n" +
                                messageTotalVolume + "\n" +
                                messageAvgVolume + "\n" +
                                messageFavouriteExercise
                    )
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    private suspend fun calculateTotalSets(): Int = withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")
        val allSets = RepositoryLocator.setRepository.getSetList(userId)
        allSets.size
    }

    private suspend fun calculateTotalVolume(): Double = withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")
        val allSets = RepositoryLocator.setRepository.getSetList(userId)
        allSets.sumOf { it.weight * it.reps }
    }

    private suspend fun calculateAverageVolume(): String = withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")
        val allSets = RepositoryLocator.setRepository.getSetList(userId)
        if (allSets.isNotEmpty()) {
            val average = allSets.sumOf { it.weight * it.reps } / allSets.size
            String.format(
                "%.2f",
                average
            ) // Zaokrąglenie do dwóch miejsc po przecinku i konwersja do Double
        } else {
            "0.0"
        }
    }

    private suspend fun calculateFavoriteExercise(): String = withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")
        val allSets = RepositoryLocator.setRepository.getSetList(userId)
        val exerciseCounts = allSets.groupingBy { it.exerciseID }.eachCount()
        val favoriteExerciseId = exerciseCounts.maxByOrNull { it.value }?.key
        if (favoriteExerciseId != null) {
            val exercise = RepositoryLocator.exerciseRepository.getExerciseById(
                userId,
                favoriteExerciseId.toString()
            )
            exercise?.name ?: String.format(getString(R.string.exercise_deleted_from_db))
        } else {
            String.format(getString(R.string.no_data))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.language_settings -> {
                languageMenu()
                true
            }

            R.id.reset_database -> {
                resetDatabase()
                true
            }

            R.id.change_theme -> {
                themeMenu()
                true
            }

            R.id.Log_out -> {
                logout()
                true
            }

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

                R.id.Light -> {
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

    private fun resetDatabase() {
       gymDb.planWrite()
       gymDb.exerciseWrite()

    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
