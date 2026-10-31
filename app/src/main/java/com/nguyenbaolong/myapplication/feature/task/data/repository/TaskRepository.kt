package com.nguyenbaolong.myapplication.feature.task.data.repository

import com.nguyenbaolong.myapplication.feature.task.data.local.TaskDao
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TaskRepository(private val dao: TaskDao) {
    fun getTasksForDate(date: LocalDate): Flow<List<TaskEntity>> =
        dao.getTasksByDate(date.toEpochDay())

    suspend fun addTask(task: TaskEntity) = dao.insert(task)
    suspend fun updateTask(task: TaskEntity) = dao.update(task)
    suspend fun deleteTask(task: TaskEntity) = dao.delete(task)
    suspend fun deleteAllTasks() = dao.deleteAll()
}
