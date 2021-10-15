package com.jose.todopriority.data.database.dao

import androidx.room.*
import com.jose.todopriority.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tb_tasks")
    fun findAll(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: Task): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: Task)

    @Delete
    suspend fun delete(entity: Task)
}