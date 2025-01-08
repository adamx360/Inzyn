package com.example.inzyn.model

data class Set(
    val id: String = "",
    val exerciseID: String = "",
    val exerciseName: String = "",
    val weight: Double = 0.0,
    val reps: Int = 0,
    val date: String = "",
    val description: String? = ""
)
