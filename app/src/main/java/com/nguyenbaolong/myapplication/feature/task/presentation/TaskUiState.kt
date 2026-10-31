package com.nguyenbaolong.myapplication.feature.task.presentation

import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity

sealed interface TaskUiState {
    object Loading : TaskUiState
    data class Success(val tasks: List<TaskEntity>) : TaskUiState
}
