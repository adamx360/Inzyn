package com.example.inzyn.data

import com.example.inzyn.model.Plan
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class PlanRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    suspend fun getPlanList(userId: String): List<Plan> {
        return try {
            val snapshot = database.child("users").child(userId).child("plans").get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { planSnapshot ->
                    planSnapshot.getValue(Plan::class.java)
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch plans for user $userId: ${e.message}")
        }
    }

    suspend fun add(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        val planId = plan.id.ifEmpty {
            database.child("users").child(sanitizedUserId).child("plans").push().key
                ?: throw Exception("Fialed to generate unique ID")
        }
        val planWithId = plan.copy(id = planId)
        database.child("users").child(sanitizedUserId).child("plans").setValue(planWithId).await()


    }

    suspend fun getPlanById(userId: String, planId: String): Plan? {
        val sanitizedUserId = sanitizeFirebaseKey(userId)

        return try {
            val snapshot =
                database.child("users").child(sanitizedUserId).child("plans").child(planId).get()
                    .await()
            if (snapshot.exists()) {
                snapshot.getValue(Plan::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch plan with ID $planId for user $userId: ${e.message}")
        }
    }

    suspend fun set(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)

        database.child("users").child(sanitizedUserId).child("plans").child(plan.id)
            .setValue(plan).await()

    }

    suspend fun removeById(userId: String, planId: String) {
        database.child("users").child(userId).child("plans").child(planId).removeValue().await()
    }


    private fun sanitizeFirebaseKey(key: String): String {
        return key.replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }

}