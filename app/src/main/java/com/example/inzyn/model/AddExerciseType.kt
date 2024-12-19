package com.example.inzyn.model

import java.io.Serializable

sealed class AddExerciseType : Serializable {
    data object New : AddExerciseType() {
        private fun readResolve(): Any = New
    }

    data class Edit(val id: String) : AddExerciseType()
}