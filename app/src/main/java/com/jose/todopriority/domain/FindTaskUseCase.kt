package com.jose.todopriority.domain

import com.jose.todopriority.core.UseCase
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class FindTaskUseCase(
    private val repository: TaskRepository
): UseCase<Long, Task>() {
    override suspend fun execute(param: Long): Flow<Task> {
        return repository.taskById(param)
    }
}