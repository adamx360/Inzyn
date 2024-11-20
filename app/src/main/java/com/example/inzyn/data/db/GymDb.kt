package com.example.inzyn.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.inzyn.data.DateConverter
import com.example.inzyn.model.db.GymEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Database(
    entities = [GymEntity::class],
    version = 1,
)
@TypeConverters(DateConverter::class)
abstract class GymDb : RoomDatabase() {
    abstract val gym: GymDao

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
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.gym)
                    }
                }
            }

            suspend fun populateDatabase(gymDao: GymDao) {
                val entries = listOf(
                    GymEntity(name = "BenchPress", reps = 1, weight = 0, date = Date()),
                    GymEntity(name = "Squat", reps = 2, weight = 0, date = Date()),
                    GymEntity(name = "Deadlift", reps = 3, weight = 0, date = Date())
                )
                entries.forEach { gymDao.createOrUpdate(it) }
                println("Database populated with: $entries") // Debug
            }
        }
    }
}
