package com.example.inzyn.data

import com.example.inzyn.model.Gym

interface GymRepository {

    suspend fun getGymList(): List<Gym>
    suspend fun add(exercise: Gym)
    suspend fun getGymById(id: Int): Gym
    suspend fun set(exercise: Gym)
    suspend fun removeById(id: Int)

    companion object{
        const val GENERATE_ID = 0
    }
}