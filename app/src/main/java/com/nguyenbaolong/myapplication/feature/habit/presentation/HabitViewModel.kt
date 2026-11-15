package com.nguyenbaolong.myapplication.feature.habit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitCompletionEntity
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.habit.domain.HabitWithStreak
import com.nguyenbaolong.myapplication.feature.habit.domain.calculateStreaks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HabitUiItem(
    val habit: HabitEntity,
    val completedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionDates: List<LocalDate> = emptyList()
)

sealed interface HabitUiState {
    object Loading : HabitUiState
    data class Success(val habits: List<HabitUiItem>) : HabitUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val uiState: StateFlow<HabitUiState> = repository.getAllHabits()
        .flatMapLatest { habits ->
            if (habits.isEmpty()) return@flatMapLatest flowOf(HabitUiState.Success(emptyList()))
            val completionFlows = habits.map { habit ->
                repository.getCompletionsForHabit(habit.id).map { completions ->
                    val dates = completions.map { it.completedDate }
                    val today = LocalDate.now()
                    val (current, best) = calculateStreaks(dates = dates, today = today, habit = habit)
                    HabitUiItem(
                        habit = habit,
                        completedToday = dates.contains(today),
                        currentStreak = current,
                        bestStreak = best,
                        completionDates = dates
                    )
                }
            }
            combine(completionFlows) { items -> HabitUiState.Success(items.toList()) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitUiState.Loading)

    fun addHabit(
        name: String,
        cue: String?,
        targetPerWeek: Int = 7,
        frequencyType: String = "DAILY",
        targetDays: String = ""
    ) {
        viewModelScope.launch {
            repository.addHabit(
                HabitEntity(
                    name = name,
                    cue = cue,
                    targetCompletionsPerWeek = targetPerWeek.coerceIn(1, 7),
                    frequencyType = frequencyType,
                    targetDays = targetDays
                )
            )
        }
    }

    fun toggleToday(habitId: Long) {
        viewModelScope.launch {
            repository.toggleCompletion(habitId, LocalDate.now())
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun updateHabit(
        habit: HabitEntity,
        newName: String = habit.name,
        newCue: String? = habit.cue,
        newTargetPerWeek: Int = habit.targetCompletionsPerWeek,
        newFrequencyType: String = habit.frequencyType,
        newTargetDays: String = habit.targetDays
    ) {
        viewModelScope.launch {
            repository.updateHabit(
                habit.copy(
                    name = newName,
                    cue = newCue,
                    targetCompletionsPerWeek = newTargetPerWeek.coerceIn(1, 7),
                    frequencyType = newFrequencyType,
                    targetDays = newTargetDays
                )
            )
        }
    }
}
