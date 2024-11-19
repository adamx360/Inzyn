package com.example.inzyn.data

import com.example.inzyn.model.Gym

object GymRepositoryInMemory: GymRepository {

    private val gymList = mutableListOf<Gym>(

    )


    override suspend fun getGymList(): List<Gym> = gymList.toList()

    override suspend fun add(exercise: Gym) {
        gymList.add(if(exercise.id == 0) exercise.copy(id = getNextId())else exercise)
    }

    override suspend fun getGymById(id: Int): Gym =
        gymList.first{it.id == id}

    override suspend fun set(exercise: Gym) {
       val index = gymList.indexOfFirst { it.id == exercise.id }
        gymList[index] = exercise
    }

    override suspend fun removeById(id: Int) {
        gymList.removeIf { it.id == id }

    }


    private fun getNextId(): Int {
        return gymList.maxOf { it.id }.inc()
    }
}