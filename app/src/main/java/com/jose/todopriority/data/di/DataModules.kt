package com.jose.todopriority.data.di

import com.jose.todopriority.data.database.AppDatabase
import com.jose.todopriority.data.repository.TaskRepository
import com.jose.todopriority.data.repository.TaskRepositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DataModules {

    fun load() {
        loadKoinModules(databaseModule())
    }

    private fun repositoryModule(): Module {
        return module {
            single<TaskRepository> { TaskRepositoryImpl(get()) }
        }
    }

    private fun databaseModule(): Module {
        return module {
            single { AppDatabase.getInstance(androidApplication()) }
        }
    }
}