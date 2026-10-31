package com.nguyenbaolong.myapplication.feature.task.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _date = MutableStateFlow(LocalDate.now())
    val date: StateFlow<LocalDate> = _date.asStateFlow()

    val uiState: StateFlow<TaskUiState> = _date
        .flatMapLatest { date -> repository.getTasksForDate(date) }
        .map<List<TaskEntity>, TaskUiState> { TaskUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TaskUiState.Loading)

    fun addTask(title: String, timeOfDay: TimeOfDay?) {
        viewModelScope.launch {
            repository.addTask(
                TaskEntity(title = title, timeOfDay = timeOfDay, taskDate = _date.value)
            )
        }
    }

    fun toggleComplete(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    fun updateTask(task: TaskEntity, newTitle: String, newTimeOfDay: TimeOfDay?) {
        viewModelScope.launch {
            repository.updateTask(task.copy(title = newTitle, timeOfDay = newTimeOfDay))
        }
    }
}
