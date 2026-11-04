package com.nguyenbaolong.myapplication.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.journal.data.repository.JournalRepository
import com.nguyenbaolong.myapplication.feature.settings.SettingsManager
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository

class ProfileViewModelFactory(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val journalRepository: JournalRepository,
    private val settingsManager: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(taskRepository, habitRepository, journalRepository, settingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

