package com.example.inzyn.data

import com.example.inzyn.model.Plan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlanRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
       currentUserId?.let {
            database.child("users")
                .child(it)
                .child("plans")
                .keepSynced(true)
        }
    }

    suspend fun getPlanList(
        userId: String,
        database: DatabaseReference = this.database
    ): List<Plan> {
        return suspendCoroutine { continuation ->
            database.child("users")
                .child(userId)
                .child("plans")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val plans = snapshot.children.mapNotNull {
                            it.getValue(Plan::class.java)
                        }
                        continuation.resume(plans)
                    } else {
                        continuation.resume(emptyList())
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(
                        Exception(
                            "Failed to fetch plans for user $userId: ${exception.message}",
                            exception
                        )
                    )
                }
        }
    }

    fun add(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        val planId = plan.id.ifEmpty {
            database.child("users")
                .child(sanitizedUserId)
                .child("plans")
                .push()
                .key
                ?: throw Exception("Failed to generate unique plan ID")
        }
        val planWithId = plan.copy(id = planId)

        database.child("users")
            .child(sanitizedUserId)
            .child("plans")
            .child(planId)
            .setValue(planWithId)
    }

    suspend fun getPlanById(userId: String, planId: String): Plan? {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        return try {
            val snapshot = database.child("users")
                .child(sanitizedUserId)
                .child("plans")
                .child(planId)
                .get()
                .await()
            if (snapshot.exists()) {
                snapshot.getValue(Plan::class.java)
            } else null
        } catch (e: Exception) {
            throw Exception("Failed to fetch plan with ID $planId for user $userId: ${e.message}", e)
        }
    }

    fun set(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        database.child("users")
            .child(sanitizedUserId)
            .child("plans")
            .child(plan.id)
            .setValue(plan)
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