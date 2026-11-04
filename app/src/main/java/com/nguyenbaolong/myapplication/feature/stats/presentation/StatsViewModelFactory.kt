package com.nguyenbaolong.myapplication.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.journal.data.repository.JournalRepository
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository

class StatsViewModelFactory(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val journalRepository: JournalRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatsViewModel(taskRepository, habitRepository, journalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

