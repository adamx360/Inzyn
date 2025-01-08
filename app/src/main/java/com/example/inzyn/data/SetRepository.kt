package com.example.inzyn.data

import com.example.inzyn.model.Set
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SetRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        currentUserId?.let {
            database.child("users")
                .child(it)
                .child("sets")
                .keepSynced(true)
        }
    }

    suspend fun getSetList(
        userId: String,
        database: DatabaseReference = this.database
    ): List<Set> {
        return suspendCoroutine { continuation ->
            database.child("users")
                .child(userId)
                .child("sets")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val sets = snapshot.children.mapNotNull {
                            it.getValue(Set::class.java)
                        }
                        continuation.resume(sets)
                    } else {
                        continuation.resume(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        Exception(
                            "Failed to fetch sets for user $userId: ${exception.message}",
                            exception
                        )
                    )
                }
        }
    }

    fun add(userId: String, set: Set) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        val setId = set.id.ifEmpty {
            database.child("users")
                .child(sanitizedUserId)
                .child("sets")
                .push().key
                ?: throw Exception("Failed to generate unique set ID")
        }
        val setWithId = set.copy(id = setId)

        database.child("users")
            .child(sanitizedUserId)
            .child("sets")
            .child(setId)
            .setValue(setWithId)
    }

    suspend fun getSetById(userId: String, setId: String): Set? {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        return try {
            val snapshot = database.child("users")
                .child(sanitizedUserId)
                .child("sets")
                .child(setId)
                .get()
                .await()
            if (snapshot.exists()) {
                snapshot.getValue(Set::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch set with ID $setId for user $userId: ${e.message}", e)
        }
    }

    fun set(userId: String, set: Set) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        database.child("users")
            .child(sanitizedUserId)
            .child("sets")
            .child(set.id)
            .setValue(set)
    }

    fun removeById(userId: String, setId: String) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        database.child("users")
            .child(sanitizedUserId)
            .child("sets")
            .child(setId)
            .removeValue()
    }

    private fun sanitizeFirebaseKey(key: String): String {
        return key
            .replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }
}