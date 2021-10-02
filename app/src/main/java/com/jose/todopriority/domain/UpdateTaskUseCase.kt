package com.jose.todopriority.domain

import com.jose.todopriority.core.UseCase
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateTaskUseCase(
    private val repository: TaskRepository
): UseCase.NoSource<Task>() {
    override suspend fun execute(param: Task): Flow<Unit> {
        return flow {
            repository.update(param)
            emit(Unit)
        }
    }
}