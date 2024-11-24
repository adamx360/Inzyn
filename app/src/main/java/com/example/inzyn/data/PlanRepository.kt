package com.example.inzyn.data

import com.example.inzyn.model.Plan

interface PlanRepository {
    suspend fun getPlanList(): List<Plan>
    suspend fun add(plan: Plan)
    suspend fun getPlanById(id: Int): Plan
    suspend fun set(plan: Plan)
    suspend fun removeById(id: Int)

    companion object {
        const val GENERATE_ID = 0
    }
}