package com.jose.todopriority.data.repository

import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun save(task: Task): Flow<Long>
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    fun list(): Flow<List<Task>>
}