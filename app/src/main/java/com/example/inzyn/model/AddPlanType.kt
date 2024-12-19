package com.example.inzyn.model

import java.io.Serializable

sealed class AddPlanType : Serializable {
    data object New : AddPlanType() {
        private fun readResolve(): Any = New
    }

    data class Edit(val id: String) : AddPlanType()
}