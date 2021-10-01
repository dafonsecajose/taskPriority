package com.jose.todopriority.domain

import com.jose.todopriority.core.UseCase
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ListTaskUseCase(
    private val repository: TaskRepository
): UseCase.NoParam<List<Task>>() {
    override suspend fun execute(): Flow<List<Task>> {
        return repository.list()
    }
}