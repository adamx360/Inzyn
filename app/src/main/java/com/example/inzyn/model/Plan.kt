package com.example.inzyn.model

data class Plan(
    val id: String = "",
    val name: String = "",
    val exercisesIDs: List<String> = emptyList()
)