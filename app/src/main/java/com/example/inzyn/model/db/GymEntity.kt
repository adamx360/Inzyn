package com.example.inzyn.model.db

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inzyn.model.Gym
import java.util.Date


@Entity(tableName = "gym")
data class GymEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: Date,
    val reps: Int,
    val weight: Int

) {

    fun toGym(context: Context): Gym {
        return Gym(id, name, reps, weight, date)
    }

    companion object {
        fun Gym.toEntity(): GymEntity {
            return GymEntity(id, name, date, reps, weigth)
        }
    }

}