package com.example.inzyn.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inzyn.model.db.ExerciseEntity

@Dao
interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdate(gym: ExerciseEntity)

    @Query("SELECT * FROM exercise;")
    suspend fun getAll(): List<ExerciseEntity>

    @Query("SELECT * FROM exercise WHERE id = :id;")
    suspend fun getById(id: Long): ExerciseEntity

    @Query("DELETE FROM exercise WHERE id = :exerciseId")
    suspend fun remove(exerciseId: Long)
}
