package com.example.inzyn.data

import com.example.inzyn.model.Gym
import java.time.LocalTime
import java.util.Date

object GymRepositoryInMemory: GymRepository {

    private val gymList = mutableListOf<Gym>(
        Gym(1, "Bicep Curl", 0, 0, Date(LocalTime.now().toString()))
    )


    override suspend fun getGymList(): List<Gym> = gymList.toList()

    override suspend fun add(exercise: Gym) {
        gymList.add(if(exercise.id == 0) exercise.copy(id = getNextId())else exercise)
    }

    override suspend fun getGymById(id: Int): Gym {
        return gymList.find { it.id == id }
            ?: throw NoSuchElementException("Note with id $id not found")
    }

    override suspend fun set(exercise: Gym) {
        val index = gymList.indexOfFirst { it.id == exercise.id }
        gymList[index] = exercise
    }

    override suspend fun removeById(id: Int) {
        gymList.removeIf { it.id == id }

    }


    private fun getNextId(): Int {
        return gymList.maxOfOrNull { it.id }?.inc() ?: 1
    }
}