package com.jose.todopriority.presentation.di

import com.jose.todopriority.presentation.AddTaskViewModel
import com.jose.todopriority.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object PresetationModule {

    fun load() {
        loadKoinModules(viewModelModules())
    }

    private fun viewModelModules(): Module {
        return module {
            viewModel { MainViewModel(get(), get()) }
            viewModel { AddTaskViewModel(get(), get(), get(), get()) }
        }
    }
}