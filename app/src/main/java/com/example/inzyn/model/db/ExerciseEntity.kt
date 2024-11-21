package com.example.inzyn.model.db

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inzyn.model.Exercise


@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String?
) {

    fun toGym(context: Context): Exercise {
        return Exercise(id, name, description)
    }

    companion object {
        fun Exercise.toEntity(): ExerciseEntity {
            return ExerciseEntity(id, name, description)
        }
    }

}