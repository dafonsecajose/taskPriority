package com.jose.todopriority.data.repository

import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun save(task: Task)
    fun list(): Flow<List<Task>>
}