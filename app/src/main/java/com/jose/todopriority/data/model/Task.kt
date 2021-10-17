package com.jose.todopriority.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tb_tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val hour: String,
    val date: String,
    val description: String,
    val priority: String
):Serializable