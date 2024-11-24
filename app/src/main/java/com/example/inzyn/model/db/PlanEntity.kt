package com.example.inzyn.model.db

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.inzyn.model.Plan

@Entity(tableName = "plan")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val exercisesIDs: List<Int>,
) {

    fun toPlan(context: Context): Plan {
        return Plan(id, name, exercisesIDs)
    }

    companion object {
        fun Plan.toEntity(): PlanEntity {
            return PlanEntity(id, name, exercisesIDs)
        }
    }

}