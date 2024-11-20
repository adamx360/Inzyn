package com.example.inzyn.data

import android.content.Context
import com.example.inzyn.data.db.GymDb
import com.example.inzyn.model.Gym
import com.example.inzyn.model.db.GymEntity.Companion.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GymRepositoryInFile(val context: Context, scope: CoroutineScope) : GymRepository {
    val db: GymDb = GymDb.open(context, scope)

    override suspend fun getGymList(): List<Gym> = withContext(Dispatchers.IO) {
        val gyms = db.gym.getAll()
        println("Loaded gyms from DB: $gyms") // Debug
        gyms.map { it.toGym(context) }
    }

    override suspend fun add(exercise: Gym) = withContext(Dispatchers.IO) {
        db.gym.createOrUpdate(exercise.toEntity())
    }

    override suspend fun getGymById(id: Int): Gym = withContext(Dispatchers.IO) {
        db.gym.getById(id.toLong()).toGym(context)
    }

    override suspend fun set(exercise: Gym) = withContext(Dispatchers.IO) {
        db.gym.createOrUpdate(exercise.toEntity())
    }

    override suspend fun removeById(id: Int) = withContext(Dispatchers.IO) {
        db.gym.remove(id.toLong())
    }
}
