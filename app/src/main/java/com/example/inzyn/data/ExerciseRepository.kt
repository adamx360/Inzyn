package com.example.inzyn.data

import com.example.inzyn.model.Exercise

interface ExerciseRepository {

    suspend fun getExerciseList(): List<Exercise>
    suspend fun add(exercise: Exercise)
    suspend fun getExerciseById(id: Int): Exercise?
    suspend fun set(exercise: Exercise)
    suspend fun removeById(id: Int)

    companion object{
        const val GENERATE_ID = 0
    }
}