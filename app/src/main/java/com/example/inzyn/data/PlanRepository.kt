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
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    init{

        database.child("users").child(userId.toString()).child("plans").keepSynced(true)

    }

    suspend fun getPlanList(userId: String,database:DatabaseReference): List<Plan> {
        return suspendCoroutine { continuation ->
            database.child("users").child(userId).child("plans")
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val plans = snapshot.children.mapNotNull { it.getValue(Plan::class.java) }
                        continuation.resume(plans)
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

     fun add(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)
        val planId = plan.id.ifEmpty {
            database.child("users").child(sanitizedUserId).child("plans").push().key
                ?: throw Exception("Fialed to generate unique ID")
        }
        val planWithId = plan.copy(id = planId)
        database.child("users").child(sanitizedUserId).child("plans").setValue(planWithId)

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

     fun set(userId: String, plan: Plan) {
        val sanitizedUserId = sanitizeFirebaseKey(userId)

        database.child("users").child(sanitizedUserId).child("plans").child(plan.id)
            .setValue(plan)

    }

     fun removeById(userId: String, planId: String) {
        database.child("users").child(userId).child("plans").child(planId).removeValue()
    }


    private fun sanitizeFirebaseKey(key: String): String {
        return key.replace(".", "_")
            .replace("#", "_")
            .replace("$", "_")
            .replace("[", "_")
            .replace("]", "_")
    }

}