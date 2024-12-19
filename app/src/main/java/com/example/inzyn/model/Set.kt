package com.example.inzyn.model

data class Set(
    var id: String = "",
    var exerciseID: String = "",
    var exerciseName: String = "",
    var weight: Double = 0.0,
    var reps: Int = 0,
    var date: String = "",
    var description: String? = "",
)
