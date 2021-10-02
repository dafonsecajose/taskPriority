package com.jose.todopriority.data.repository

import com.jose.todopriority.data.database.AppDatabase
import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    appDatabase: AppDatabase
): TaskRepository {

    private val dao = appDatabase.taskDao()

    override suspend fun save(task: Task) {
        dao.save(task)
    }

    override suspend fun update(task: Task) {
        dao.update(task)
    }

    override suspend fun delete(task: Task) {
        dao.delete(task)
    }

    override fun list(): Flow<List<Task>> {
        return dao.findAll()
    }
}