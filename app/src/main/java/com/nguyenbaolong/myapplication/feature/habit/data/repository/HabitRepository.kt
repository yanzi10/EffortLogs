package com.nguyenbaolong.myapplication.feature.habit.data.repository

import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitCompletionEntity
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitDao
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HabitRepository(private val dao: HabitDao) {
    fun getAllHabits(): Flow<List<HabitEntity>> = dao.getAllHabits()
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletionEntity>> =
        dao.getCompletionsForHabit(habitId)

    suspend fun addHabit(habit: HabitEntity) = dao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = dao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = dao.deleteHabit(habit)

    suspend fun toggleCompletion(habitId: Long, date: LocalDate) {
        val epochDay = date.toEpochDay()
        val existing = dao.getCompletion(habitId, epochDay)
        if (existing != null) {
            dao.deleteCompletion(habitId, epochDay)
        } else {
            dao.insertCompletion(HabitCompletionEntity(habitId = habitId, completedDate = date))
        }
    }

    suspend fun deleteAll() {
        dao.deleteAllCompletions()
        dao.deleteAllHabits()
    }
}
