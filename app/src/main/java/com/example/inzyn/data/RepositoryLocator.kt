package com.example.inzyn.data

object RepositoryLocator {
    val exerciseRepository: ExerciseRepository by lazy { ExerciseRepository() }
    val planRepository: PlanRepository by lazy { PlanRepository() }
    val setRepository: SetRepository by lazy { SetRepository() }
}
