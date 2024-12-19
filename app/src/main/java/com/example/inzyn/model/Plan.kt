package com.example.inzyn.model

data class Plan(
    var id: String = "",
    var name: String = "",
    var exercisesIDs: List<String> = listOf(),
)