package com.example.inzyn.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope

object RepositoryLocator {
    lateinit var exerciseRepository: ExerciseRepository
        private set

    fun init(context: Context, scope: CoroutineScope){
        exerciseRepository = ExerciseRepositoryInFile(context, scope)
    }
}