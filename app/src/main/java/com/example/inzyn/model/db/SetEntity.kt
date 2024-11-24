package com.example.inzyn.model.db

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inzyn.model.Set

@Entity(tableName = "set")
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseID: Int,
    val weight: Double,
    val reps: Int,
    val date: String,
    val description: String?,
) {

    fun toSet(context: Context): Set {
        return Set(id, exerciseID, weight, reps, date, description)
    }

    companion object {
        fun Set.toEntity(): SetEntity {
            return SetEntity(id, exerciseID, weight, reps, date, description)
        }
    }

}