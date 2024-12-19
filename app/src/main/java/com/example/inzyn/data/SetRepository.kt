package com.example.inzyn.data

import com.example.inzyn.model.Set
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class SetRepository {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun getSetList(userId: String): List<Set> {
        return try {
            val snapshot = database.child("users").child(userId).child("sets").get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { setSnapshot ->
                    setSnapshot.getValue(Set::class.java)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch set for user $userId: ${e.message} ")
        }

    }

    suspend fun add(userId: String, set: Set) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)


        val setId = set.id.ifEmpty {
            database.child("users").child(sanitizedUserId).child("sets").push().key
                ?: throw Exception("Failed to generate unique exercise ID")
        }

        val setWithId = set.copy(id = setId)
        database.child("users").child(sanitizedUserId).child("sets").child(setId)
            .setValue(setWithId).await()
    }

    suspend fun getSetById(userId: String, setId: String): Set? {
        val sanitizedUserId = sanitizeFirebaseKey(userId)


        return try {
            val snapshot =
                database.child("users").child(sanitizedUserId).child("sets").child(setId).get()
                    .await()
            if (snapshot.exists()) {
                snapshot.getValue(Set::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch set with ID $setId for user $userId: ${e.message} ")
        }
    }

    suspend fun set(userId: String, set: Set) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)

        database.child("users").child(sanitizedUserId).child("sets").child(set.id).setValue(set)
            .await()
    }

    suspend fun removeById(userId: String, setId: String) {

        database.child("users").child(userId).child("sets").child(setId).removeValue().await()
    }

    private fun sanitizeFirebaseKey(key: String): String {
        return key.replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }


}