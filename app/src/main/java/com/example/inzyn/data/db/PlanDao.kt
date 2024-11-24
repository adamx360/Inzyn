package com.example.inzyn.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inzyn.model.db.PlanEntity


@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdate(plan: PlanEntity)

    @Query("SELECT * FROM `plan`;")
    suspend fun getAll(): List<PlanEntity>

    @Query("SELECT * FROM `plan` WHERE id = :id;")
    suspend fun getById(id: Long): PlanEntity

    @Query("DELETE FROM `plan` WHERE id = :planId")
    suspend fun remove(planId: Long)
}