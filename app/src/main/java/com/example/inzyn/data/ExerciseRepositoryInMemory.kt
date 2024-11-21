package com.example.inzyn.data

import com.example.inzyn.model.Exercise

object ExerciseRepositoryInMemory: ExerciseRepository {

    private val exerciseLists = mutableListOf<Exercise>(
        Exercise(1, "Bicep Curl", "Description for Bicep Curl")
    )


    override suspend fun getExerciseList(): List<Exercise> = exerciseLists.toList()

    override suspend fun add(exercise: Exercise) {
        exerciseLists.add(if(exercise.id == 0) exercise.copy(id = getNextId())else exercise)
    }

    override suspend fun getExerciseById(id: Int): Exercise {
        return exerciseLists.find { it.id == id }
            ?: throw NoSuchElementException("Exercise with id $id not found")
    }

    override suspend fun set(exercise: Exercise) {
        val index = exerciseLists.indexOfFirst { it.id == exercise.id }
        exerciseLists[index] = exercise
    }

    override suspend fun removeById(id: Int) {
        exerciseLists.removeIf { it.id == id }

    }


    private fun getNextId(): Int {
        return exerciseLists.maxOfOrNull { it.id }?.inc() ?: 1
    }
}