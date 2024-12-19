package com.example.inzyn.data

import com.example.inzyn.model.Exercise
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ExerciseRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference


    suspend fun add(userId: String, exercise: Exercise) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)


        val exerciseId = exercise.id.ifEmpty {
            database.child("users").child(sanitizedUserId).child("exercises").push().key
                ?: throw Exception("Failed to generate unique exercise ID")
        }

        val exerciseWithId = exercise.copy(id = exerciseId)
        database.child("users").child(sanitizedUserId).child("exercises").child(exerciseId)
            .setValue(exerciseWithId).await()

    }


    suspend fun getExerciseList(userId: String): List<Exercise> {
        return try {
            val snapshot = database.child("users").child(userId).child("exercises").get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { exerciseSnapshot ->
                    exerciseSnapshot.getValue(Exercise::class.java)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch exercises for user $userId: ${e.message}")
        }
    }

    suspend fun set(userId: String, exercise: Exercise) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)


        try {
            if (exercise.id.isEmpty()) {
                throw Exception("Dish ID cannot be null or empty for updating")
            }

            database.child("users").child(sanitizedUserId).child("exercises").child(exercise.id)
                .setValue(exercise).await()

        } catch (e: Exception) {
            throw Exception("Failed to update exercise for user $userId: ${e.message}")
        }

    }

    suspend fun removeById(userId: String, exerciseId: String) {
        try {
            database.child("users").child(userId).child("exercises").child(exerciseId).removeValue()
                .await()

        } catch (e: Exception) {
            throw Exception("Failed to remove exercise for user $userId: ${e.message}")
        }
    }

    suspend fun getExerciseById(userId: String, exerciseId: String): Exercise? {
        val sanitizedUserId = sanitizeFirebaseKey(userId)

        return try {
            val snapshot =
                database.child("users").child(sanitizedUserId).child("exercises").child(exerciseId)
                    .get().await()
            if (snapshot.exists()) {
                snapshot.getValue(Exercise::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch exercise with ID $exerciseId for user $userId: ${e.message}")
        }
    }

    private fun sanitizeFirebaseKey(key: String): String {
        return key.replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }

}