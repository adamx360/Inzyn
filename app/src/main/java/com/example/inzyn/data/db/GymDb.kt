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
                    ExerciseEntity(name = "Deadlift", description = "Description for Deadlift")
                )
                exerciseEntities.forEach { exerciseDao.createOrUpdate(it) }
                println("Database populated with exercises: $exerciseEntities")

                val planEntities = listOf(
                    PlanEntity(name = "Chest", exercisesIDs = emptyList()),
                    PlanEntity(name = "Legs", exercisesIDs = emptyList()),
                    PlanEntity(name = "Back", exercisesIDs = emptyList()),
                    PlanEntity(name = "Abs", exercisesIDs = emptyList()),
                    PlanEntity(name = "Shoulders", exercisesIDs = emptyList()),
                    PlanEntity(name = "Biceps", exercisesIDs = emptyList()),
                    PlanEntity(name = "Triceps", exercisesIDs = emptyList())
                )
                planEntities.forEach { planDao.createOrUpdate(it) }
                println("Database populated with plans: $planEntities")
            }
        }
    }
}
