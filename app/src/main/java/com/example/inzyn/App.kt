package com.example.inzyn

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.database.database

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.database.setPersistenceEnabled(true)
    }

}