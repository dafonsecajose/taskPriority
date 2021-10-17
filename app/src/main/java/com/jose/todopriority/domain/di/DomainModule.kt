package com.jose.todopriority.domain.di

import com.jose.todopriority.domain.*
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
            factory { FindTaskUseCase(get()) }
            factory { SaveTaskUseCase(get()) }
            factory { LastTaskIdUseCase(get()) }
            factory { UpdateTaskUseCase(get()) }
            factory { DeleteTaskUseCase(get()) }
        }
    }
}