package com.nguyenbaolong.myapplication.feature.stats.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nguyenbaolong.myapplication.feature.habit.data.repository.HabitRepository
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import com.nguyenbaolong.myapplication.feature.journal.data.repository.JournalRepository
import com.nguyenbaolong.myapplication.feature.task.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

data class CategoryStat(
    val category: ActivityCategory,
    val totalMinutes: Int,
    val count: Int
)

data class StatsUiState(
    val isLoading: Boolean = true,
    val todayCompletedTasks: Int = 0,
    val todayTotalTasks: Int = 0,
    val todayCompletionRate: Int = 0,
    val weekCompletedTasks: Int = 0,
    val weekTotalTasks: Int = 0,
    val weekCompletionRate: Int = 0,
    val totalHabitsCount: Int = 0,
    val habitsCompletedToday: Int = 0,
    val bestStreakAcrossHabits: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val totalMinutesThisWeek: Int = 0,
    val mostActiveDay: String? = null,
    val mostActiveCategory: ActivityCategory? = null,
    val heatmapData: Map<LocalDate, Int> = emptyMap()
)

class StatsViewModel(
    private val taskRepository: TaskRepository,
    private val habitRepository: HabitRepository,
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        viewModelScope.launch {
            combine(
                taskRepository.getTasksForDate(today),
                habitRepository.getAllHabits(),
                journalRepository.getAllLogs()
            ) { todayTasks, habits, allLogs ->
                // Today tasks
                val todayTotal = todayTasks.size
                val todayCompleted = todayTasks.count { it.isCompleted }
                val todayRate = if (todayTotal > 0) (todayCompleted * 100) / todayTotal else 0

                // Heatmap & week aggregation from logs
                val weekLogs = allLogs.filter { !it.activityDate.isBefore(monday) && !it.activityDate.isAfter(sunday) }
                val totalMinutesWeek = weekLogs.sumOf { it.durationMinutes ?: 0 }

                // Category stats for this week
                val groupedByCategory = ActivityCategory.entries.map { cat ->
                    val logsInCat = weekLogs.filter { it.category == cat }
                    CategoryStat(
                        category = cat,
                        totalMinutes = logsInCat.sumOf { it.durationMinutes ?: 0 },
                        count = logsInCat.size
                    )
                }.filter { it.count > 0 }

                val mostActiveCat = groupedByCategory.maxByOrNull { it.totalMinutes }?.category

                // Most active day in week
                val dayLogsGroup = weekLogs.groupBy { it.activityDate.dayOfWeek }
                val mostActiveDayOfWeek = dayLogsGroup.maxByOrNull { it.value.sumOf { l -> l.durationMinutes ?: 0 } }?.key?.name

                // Heatmap data for last 28 days
                val heatmap = mutableMapOf<LocalDate, Int>()
                for (i in 0..27) {
                    val d = today.minusDays(i.toLong())
                    val logCount = allLogs.count { it.activityDate == d }
                    heatmap[d] = logCount
                }

                StatsUiState(
                    isLoading = false,
                    todayCompletedTasks = todayCompleted,
                    todayTotalTasks = todayTotal,
                    todayCompletionRate = todayRate,
                    weekCompletedTasks = todayCompleted,
                    weekTotalTasks = todayTotal,
                    weekCompletionRate = todayRate,
                    totalHabitsCount = habits.size,
                    habitsCompletedToday = 0,
                    bestStreakAcrossHabits = 0,
                    categoryStats = groupedByCategory,
                    totalMinutesThisWeek = totalMinutesWeek,
                    mostActiveDay = mostActiveDayOfWeek,
                    mostActiveCategory = mostActiveCat,
                    heatmapData = heatmap
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
