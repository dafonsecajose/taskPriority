package com.jose.todopriority.data.repository

import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun save(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    fun list(): Flow<List<Task>>
    fun taskById(id: Long): Flow<Task>
    fun lastTaskId(): Flow<Long>
}