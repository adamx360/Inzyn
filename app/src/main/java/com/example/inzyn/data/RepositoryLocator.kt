package com.example.inzyn.data

import android.content.Context

object RepositoryLocator {
    lateinit var gymRepository: GymRepository
    private set

    fun init(context: Context){
        gymRepository = GymRepositoryInFile(context)
    }
}