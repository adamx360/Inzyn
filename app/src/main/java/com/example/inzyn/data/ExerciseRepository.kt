package com.example.inzyn.data

import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import com.example.inzyn.model.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ExerciseRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    init{

        database.child("users").child(userId.toString()).child("exercises").keepSynced(true)
    }

    suspend fun add(userId: String, exercise: Exercise) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        val exerciseId = exercise.id.ifEmpty {
            database.child("users").child(sanitizedUserId).child("exercises").push().key
                ?: throw Exception("Failed to generate unique exercise ID")
        }

        val exerciseWithId = exercise.copy(id = exerciseId)
        database.child("users").child(sanitizedUserId).child("exercises").child(exerciseId)
            .setValue(exerciseWithId)

    }


    suspend fun getExerciseList(userId: String,database: DatabaseReference): List<Exercise> {
        return suspendCoroutine { continuation ->
            database.child("users").child(userId).child("exercises")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val exercises = snapshot.children.mapNotNull { it.getValue(Exercise::class.java) }
                        continuation.resume(exercises)
                    } else {
                        continuation.resume(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        Exception("Failed to fetch exercises for user $userId: ${exception.message}", exception)
                    )
                }
        }
    }

    suspend fun set(userId: String, exercise: Exercise) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)


        try {
            if (exercise.id.isEmpty()) {
                throw Exception("Dish ID cannot be null or empty for updating")
            }

            database.child("users").child(sanitizedUserId).child("exercises").child(exercise.id)
                .setValue(exercise)

        } catch (e: Exception) {
            throw Exception("Failed to update exercise for user $userId: ${e.message}")
        }
        database.keepSynced(true)
    }

    suspend fun removeById(userId: String, exerciseId: String) {
        try {
            database.child("users").child(userId).child("exercises").child(exerciseId).removeValue()


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