package com.example.inzyn

import android.app.Application
import com.example.inzyn.data.RepositoryLocator

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        RepositoryLocator.init(applicationContext)
    }

}