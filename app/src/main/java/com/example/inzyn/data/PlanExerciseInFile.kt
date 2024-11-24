package com.example.inzyn.data

import android.content.Context
import com.example.inzyn.data.db.GymDb
import com.example.inzyn.model.Plan
import com.example.inzyn.model.db.PlanEntity.Companion.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlanRepositoryInFile(val context: Context, scope: CoroutineScope) : PlanRepository {
    private val db: GymDb = GymDb.open(context, scope)

    override suspend fun getPlanList(): List<Plan> = withContext(Dispatchers.IO) {
        val plans = db.plan.getAll()
        println("Loaded plans from DB: $plans") // Debug
        plans.map { it.toPlan(context) }
    }

    override suspend fun add(plan: Plan) = withContext(Dispatchers.IO) {
        db.plan.createOrUpdate(plan.toEntity())
    }

    override suspend fun getPlanById(id: Int): Plan = withContext(Dispatchers.IO) {
        db.plan.getById(id.toLong()).toPlan(context)
    }

    override suspend fun set(plan: Plan) = withContext(Dispatchers.IO) {
        db.plan.createOrUpdate(plan.toEntity())
    }

    override suspend fun removeById(id: Int) = withContext(Dispatchers.IO) {
        db.plan.remove(id.toLong())
    }
}