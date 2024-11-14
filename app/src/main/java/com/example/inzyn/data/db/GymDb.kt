package com.example.inzyn.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inzyn.model.db.GymEntity


@Database(

    entities = [GymEntity::class],
    version = 1,
)
abstract class GymDb: RoomDatabase() {
    abstract val gym: GymDao

    companion object{
        fun open(context: Context): GymDb{
            return Room.databaseBuilder(
                context,GymDb::class.java,"gym.db"
            ).build()
        }
    }


}