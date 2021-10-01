package com.jose.todopriority.application

import android.app.Application
import com.jose.todopriority.data.di.DataModules
import com.jose.todopriority.domain.di.DomainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TaskApplication)
        }

        DataModules.load()
        DomainModule.load()
    }
}