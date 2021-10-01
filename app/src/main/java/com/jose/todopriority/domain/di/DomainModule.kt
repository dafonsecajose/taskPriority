package com.jose.todopriority.domain.di

import com.jose.todopriority.domain.ListTaskUseCase
import com.jose.todopriority.domain.SaveTaskUseCase
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModule {
    fun load() {
        loadKoinModules(useCaseModules())
    }

    private fun useCaseModules(): Module {
        return module {
            factory { ListTaskUseCase(get()) }
            factory { SaveTaskUseCase(get()) }
        }
    }
}