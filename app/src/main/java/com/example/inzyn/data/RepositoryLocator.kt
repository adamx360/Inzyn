package com.example.inzyn.data

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope

object RepositoryLocator {
    val exerciseRepository: ExerciseRepository by lazy {
        ExerciseRepository()
    }
    val planRepository: PlanRepository by lazy {
        PlanRepository()
    }
    val setRepository: SetRepository by lazy {
        SetRepository()
    }
}