package com.example.inzyn.model.navigation

import androidx.navigation.NavController

class PopBack : Destination() {
    override fun navigate(controller: NavController) {
        controller.popBackStack()
    }
}