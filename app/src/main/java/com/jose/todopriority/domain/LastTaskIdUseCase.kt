package com.jose.todopriority.domain

import com.jose.todopriority.core.UseCase
import com.jose.todopriority.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class LastTaskIdUseCase(
    private val repository: TaskRepository
): UseCase.NoParam<Long>() {
    override suspend fun execute(): Flow<Long> {
        return repository.lastTaskId()
    }
}