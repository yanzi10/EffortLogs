package com.nguyenbaolong.myapplication.feature.profile.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.journal.data.repository.JournalRepository
import com.nguyenbaolong.myapplication.feature.settings.AppSettings
import com.nguyenbaolong.myapplication.feature.settings.AppThemeMode
import com.nguyenbaolong.myapplication.feature.settings.SettingsManager
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ProfileUiState(
    val taskCount: Int = 0,
    val habitCount: Int = 0,
    val activityCount: Int = 0,
    val settings: AppSettings = AppSettings()
)

class ProfileViewModel(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val journalRepository: JournalRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                taskRepository.getTasksForDate(LocalDate.now()),
                habitRepository.getAllHabits(),
                journalRepository.getAllLogs(),
                settingsManager.settings
            ) { tasks, habits, logs, settings ->
                ProfileUiState(
                    taskCount = tasks.size,
                    habitCount = habits.size,
                    activityCount = logs.size,
                    settings = settings
                )
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        settingsManager.setThemeMode(mode)
    }

    fun setStartOfDayHour(hour: Int) {
        settingsManager.setStartOfDayHour(hour)
    }

    fun setReminderEnabled(type: String, enabled: Boolean) {
        settingsManager.setReminderEnabled(type, enabled)
    }

    fun wipeData(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            taskRepository.deleteAllTasks()
            habitRepository.deleteAll()
            journalRepository.deleteAllLogs()
            settingsManager.clearAllSettings()
            onComplete?.invoke()
        }
    }

    fun exportData(context: Context, format: String) {
        viewModelScope.launch {
            val logs = journalRepository.getAllLogs().first()
            val text = if (format == "json") {
                val items = logs.joinToString(",\n  ") { log ->
                    """{"id": ${log.id}, "action": "${log.detectedAction}", "category": "${log.category.name}", "duration": ${log.durationMinutes ?: 0}, "date": "${log.activityDate}"}"""
                }
                "[\n  $items\n]"
            } else {
                val rows = logs.joinToString("\n") { log ->
                    "${log.id},\"${log.detectedAction}\",${log.category.name},${log.durationMinutes ?: 0},${log.activityDate}"
                }
                "id,action,category,duration_minutes,date\n$rows"
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export Personal Data ($format)")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        }
    }
}
