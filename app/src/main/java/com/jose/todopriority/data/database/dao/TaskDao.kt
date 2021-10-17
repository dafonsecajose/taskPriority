package com.jose.todopriority.data.database.dao

import androidx.room.*
import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tb_tasks")
    fun findAll(): Flow<List<Task>>

    @Query("SELECT * FROM tb_tasks WHERE id = :id")
    fun findTaskById(id: Long): Flow<Task>

    @Query("SELECT id FROM tb_tasks ORDER BY id DESC LIMIT 1")
    fun getLastTask(): Flow<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: Task)

    @Delete
    suspend fun delete(entity: Task)
}