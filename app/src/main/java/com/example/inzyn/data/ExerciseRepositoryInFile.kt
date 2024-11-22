package com.example.inzyn.data

import android.content.Context
import com.example.inzyn.data.db.ExerciseDb
import com.example.inzyn.model.Exercise
import com.example.inzyn.model.db.ExerciseEntity.Companion.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExerciseRepositoryInFile(val context: Context, scope: CoroutineScope) : ExerciseRepository {
    private val db: ExerciseDb = ExerciseDb.open(context, scope)

    override suspend fun getExerciseList(): List<Exercise> = withContext(Dispatchers.IO) {
        val exercises = db.exercise.getAll()
        println("Loaded exercises from DB: $exercises") // Debug
        exercises.map { it.toExercise(context) }
    }

    override suspend fun add(exercise: Exercise) = withContext(Dispatchers.IO) {
        db.exercise.createOrUpdate(exercise.toEntity())
    }

    override suspend fun getExerciseById(id: Int): Exercise = withContext(Dispatchers.IO) {
        db.exercise.getById(id.toLong()).toExercise(context)
    }

    override suspend fun set(exercise: Exercise) = withContext(Dispatchers.IO) {
        db.exercise.createOrUpdate(exercise.toEntity())
    }

    override suspend fun removeById(id: Int) = withContext(Dispatchers.IO) {
        db.exercise.remove(id.toLong())
    }
}