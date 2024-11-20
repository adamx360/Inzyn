package com.example.inzyn.model.navigation

import androidx.navigation.NavController
import java.util.concurrent.atomic.AtomicBoolean

abstract class Destination {
    private val consumed = AtomicBoolean(false)
    abstract fun navigate(controller: NavController)

    fun resolve(controller: NavController) {
        if (consumed.compareAndSet(false, true)) {
            navigate(controller)
        }
    }
}