package com.example.inzyn.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inzyn.model.db.GymEntity

@Dao
interface GymDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdate(gym: GymEntity)

    @Query("SELECT * FROM gym;")
    suspend fun getAll(): List<GymEntity>

    @Query("SELECT * FROM gym WHERE id = :id;")
    suspend fun getById(id: Long): GymEntity

    @Query("DELETE FROM gym WHERE id = :gymId")
    suspend fun remove(gymId: Long)






}