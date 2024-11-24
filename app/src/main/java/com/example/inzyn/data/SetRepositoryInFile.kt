package com.example.inzyn.data

import android.content.Context
import com.example.inzyn.data.db.GymDb
import com.example.inzyn.model.Set
import com.example.inzyn.model.db.SetEntity.Companion.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetRepositoryInFile(val context: Context, scope: CoroutineScope) : SetRepository {
    private val db: GymDb = GymDb.open(context, scope)

    override suspend fun getSetList(): List<Set> = withContext(Dispatchers.IO) {
        val set = db.set.getAll()
        println("Loaded sets from DB: $set") // Debug
        set.map { it.toSet(context) }
    }

    override suspend fun add(set: Set) = withContext(Dispatchers.IO) {
        db.set.createOrUpdate(set.toEntity())
    }

    override suspend fun getSetById(id: Int): Set = withContext(Dispatchers.IO) {
        db.set.getById(id.toLong()).toSet(context)
    }

    override suspend fun set(set: Set) = withContext(Dispatchers.IO) {
        db.set.createOrUpdate(set.toEntity())
    }

    override suspend fun removeById(id: Int) = withContext(Dispatchers.IO) {
        db.set.remove(id.toLong())
    }
}