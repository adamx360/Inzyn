package com.example.inzyn.data

import com.example.inzyn.model.Set

interface SetRepository {
    suspend fun getSetList(): List<Set>
    suspend fun add(set: Set)
    suspend fun getSetById(id: Int): Set
    suspend fun set(set: Set)
    suspend fun removeById(id: Int)

    companion object {
        const val GENERATE_ID = 0
    }
}