package com.jose.todopriority.domain

import com.jose.todopriority.core.UseCase
import com.jose.todopriority.data.model.Task
import com.jose.todopriority.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class NewSaveTaskUseCase(
    private val repository: TaskRepository
): UseCase<Task, Long>() {
    override suspend fun execute(param: Task): Flow<Long> {
           return repository.save(param)
    }

}