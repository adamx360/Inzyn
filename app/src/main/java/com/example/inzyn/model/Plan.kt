package com.example.inzyn.model

data class Plan(
    val id: Int,
    val name: String,
    val exercisesIDs: List<Int>,
)