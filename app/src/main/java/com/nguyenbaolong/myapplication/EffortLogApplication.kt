package com.nguyenbaolong.myapplication

import android.app.Application
import com.nguyenbaolong.myapplication.core.di.AppContainer

class EffortLogApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
