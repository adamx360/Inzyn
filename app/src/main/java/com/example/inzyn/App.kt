package com.example.inzyn

import android.app.Application
import com.example.inzyn.data.RepositoryLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        RepositoryLocator.init(this, CoroutineScope(Dispatchers.IO))
    }

}