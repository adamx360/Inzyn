package com.example.inzyn.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope

object RepositoryLocator {
    lateinit var gymRepository: GymRepository
        private set

    fun init(context: Context, scope: CoroutineScope){
        gymRepository = GymRepositoryInFile(context, scope)
    }
}