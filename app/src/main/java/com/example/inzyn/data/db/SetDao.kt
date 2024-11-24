package com.example.inzyn.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inzyn.model.db.SetEntity

@Dao
interface SetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createOrUpdate(plan: SetEntity)

    @Query("SELECT * FROM `set`;")
    suspend fun getAll(): List<SetEntity>

    @Query("SELECT * FROM `set` WHERE id = :id;")
    suspend fun getById(id: Long): SetEntity

    @Query("DELETE FROM `set` WHERE id = :setId")
    suspend fun remove(setId: Long)
}