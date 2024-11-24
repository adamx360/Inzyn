package com.example.inzyn.model

import java.io.Serializable

sealed class AddSetType : Serializable {
    data object New : AddSetType() {
        private fun readResolve(): Any = New
    }

    data class Edit(val id: Int) : AddSetType()
}