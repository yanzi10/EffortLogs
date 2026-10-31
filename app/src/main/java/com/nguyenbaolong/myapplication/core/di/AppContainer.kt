package com.nguyenbaolong.myapplication.core.di

import android.content.Context
import com.nguyenbaolong.myapplication.core.database.AppDatabase
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository

class AppContainer(context: Context) {
    private val db = AppDatabase.getInstance(context)
    val taskRepository = TaskRepository(db.taskDao())
    val habitRepository = HabitRepository(db.habitDao())
}
