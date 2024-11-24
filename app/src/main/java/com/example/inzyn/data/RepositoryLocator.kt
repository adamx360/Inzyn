package com.example.inzyn.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope

object RepositoryLocator {
    lateinit var exerciseRepository: ExerciseRepository
        private set
    lateinit var planRepository: PlanRepository
        private set
    lateinit var setRepository: SetRepository
        private set


    fun init(context: Context, scope: CoroutineScope){
        exerciseRepository = ExerciseRepositoryInFile(context, scope)
        planRepository = PlanRepositoryInFile(context, scope)
        setRepository = SetRepositoryInFile(context, scope)
    }
}