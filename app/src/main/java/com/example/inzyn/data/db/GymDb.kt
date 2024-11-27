package com.example.inzyn.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.inzyn.data.Converters
import com.example.inzyn.model.db.ExerciseEntity
import com.example.inzyn.model.db.PlanEntity
import com.example.inzyn.model.db.SetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [ExerciseEntity::class, PlanEntity::class, SetEntity::class],
    version = 2,
)
@TypeConverters(Converters::class)
abstract class GymDb : RoomDatabase() {
    abstract val exercise: ExerciseDao
    abstract val set: SetDao
    abstract val plan: PlanDao

    companion object {
        @Volatile
        private var INSTANCE: GymDb? = null

        fun open(context: Context, scope: CoroutineScope): GymDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDb::class.java,
                    "gym"
                ).addCallback(GymDbCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }

        private class GymDbCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.exercise, database.plan, database.set)
                    }
                }
            }

            suspend fun populateDatabase(
                exerciseDao: ExerciseDao,
                planDao: PlanDao,
                setDao: SetDao
            ) {
                val exerciseEntities = listOf(
                    ExerciseEntity(name = "BenchPress", description = "Description for BenchPress"),
                    ExerciseEntity(name = "Squat", description = "Description for Squat"),
                    ExerciseEntity(name = "Deadlift", description = "Description for Deadlift"),
                    ExerciseEntity(name = "PullUps", description = "Description for PullUps"),
                    ExerciseEntity(name = "Overhead Press", description = "Description for Overhead Press"),
                    ExerciseEntity(name = "Barbell Row", description = "Description for Barbell Row"),
                    ExerciseEntity(name = "Bicep Curls", description = "Description for Bicep Curls"),
                    ExerciseEntity(name = "Tricep Extensions", description = "Description for Tricep Extensions"),
                    ExerciseEntity(name = "Lunges", description = "Description for Lunges"),
                    ExerciseEntity(name = "Plank", description = "Description for Plank"),
                    ExerciseEntity(name = "Dips", description = "Description for Dips")
                )
                exerciseEntities.forEach { exerciseDao.createOrUpdate(it) }
                println("Database populated with exercises: $exerciseEntities")

                val planEntities = listOf(
                    PlanEntity(name = "Chest Day", exercisesIDs = listOf(1, 5, 11)), // BenchPress, Overhead Press, Dips
                    PlanEntity(name = "Leg Day", exercisesIDs = listOf(2, 9)),       // Squat, Lunges
                    PlanEntity(name = "Back Day", exercisesIDs = listOf(3, 4, 6)),   // Deadlift, PullUps, Barbell Row
                    PlanEntity(name = "Core Day", exercisesIDs = listOf(10)),        // Plank
                    PlanEntity(name = "Arms Day", exercisesIDs = listOf(7, 8)),       // Bicep Curls, Tricep Extensions
                    PlanEntity(name = "Rest Day", exercisesIDs = listOf()),
                    PlanEntity(name = "Rest Day", exercisesIDs = listOf()),
                )
                planEntities.forEach { planDao.createOrUpdate(it) }
                println("Database populated with plans: $planEntities")

                val setEntities = listOf(
                    // Sets for BenchPress
                    SetEntity(exerciseID = 1, reps = 10, weight = 60.0, date = "2024-11-21", description = "Set 1", exerciseName = "BenchPress"),
                    SetEntity(exerciseID = 1, reps = 8, weight = 65.0, date = "2024-11-21", description = "Set 2", exerciseName = "BenchPress"),
                    // Sets for Squat
                    SetEntity(exerciseID = 2, reps = 12, weight = 80.0, date = "2024-11-22", description = "Set 1", exerciseName = "Squat"),
                    SetEntity(exerciseID = 2, reps = 10, weight = 85.0, date = "2024-11-22", description = "Set 2", exerciseName = "Squat"),
                    // Sets for Deadlift
                    SetEntity(exerciseID = 3, reps = 8, weight = 100.0, date = "2024-11-22", description = "Set 1", exerciseName = "Deadlift"),
                    SetEntity(exerciseID = 3, reps = 6, weight = 110.0, date = "2024-11-22", description = "Set 2", exerciseName = "Deadlift"),
                    // Sets for PullUps
                    SetEntity(exerciseID = 4, reps = 12, weight = 0.0, date = "2024-11-23", description = "Bodyweight", exerciseName = "PullUps"),
                    SetEntity(exerciseID = 4, reps = 10, weight = 5.0, date = "2024-11-23", description = "Weighted", exerciseName = "PullUps"),
                    // Sets for Overhead Press
                    SetEntity(exerciseID = 5, reps = 8, weight = 40.0, date = "2024-11-23", description = "Set 1", exerciseName = "Overhead Press"),
                    // Sets for Bicep Curls
                    SetEntity(exerciseID = 7, reps = 10, weight = 15.0, date = "2024-11-24", description = "Set 1", exerciseName = "Bicep Curls"),
                    // Sets for Tricep Extensions
                    SetEntity(exerciseID = 8, reps = 10, weight = 20.0, date = "2024-11-25", description = "Set 1", exerciseName = "Tricep Extensions")
                )
                setEntities.forEach { setDao.createOrUpdate(it) }
                println("Database populated with sets: $setEntities")
            }
        }
    }
}
