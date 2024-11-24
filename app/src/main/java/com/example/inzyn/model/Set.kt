package com.example.inzyn.model

data class Set(
    val id: Int,
    val exerciseID: Int,
    val weight: Double,
    val reps: Int,
    val date: String,
    val description: String?,
)