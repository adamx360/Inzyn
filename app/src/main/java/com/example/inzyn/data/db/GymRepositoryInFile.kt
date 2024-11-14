package com.example.inzyn.data.db

import android.content.Context
import com.example.inzyn.data.GymRepository
import com.example.inzyn.model.Gym
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GymRepositoryInFile(val context: Context): GymRepository {
    val db:GymDb = GymDb.open(context)

    override suspend fun getGymList(): List<Gym> = withContext(Dispatchers.IO){
        db.gym.getAll().map{it.toGym(context)}
    }

}