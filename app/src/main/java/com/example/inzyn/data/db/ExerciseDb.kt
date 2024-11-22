package com.example.inzyn.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.inzyn.data.DateConverter
import com.example.inzyn.model.db.ExerciseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [ExerciseEntity::class],
    version = 1,
)
@TypeConverters(DateConverter::class)
abstract class ExerciseDb : RoomDatabase() {
    abstract val exercise: ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: ExerciseDb? = null

        fun open(context: Context, scope: CoroutineScope): ExerciseDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ExerciseDb::class.java,
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
                        populateDatabase(database.exercise)
                    }
                }
            }

            suspend fun populateDatabase(exerciseDao: ExerciseDao) {
                val entries = listOf(
                    ExerciseEntity(name = "BenchPress", description = "Description for BenchPress"),
                    ExerciseEntity(name = "Squat", description = "Description for Squat"),
                    ExerciseEntity(name = "Deadlift", description = "Description for Deadlift")
                )
                entries.forEach { exerciseDao.createOrUpdate(it) }
                println("Database populated with: $entries") // Debug
            }
        }
    }
}
