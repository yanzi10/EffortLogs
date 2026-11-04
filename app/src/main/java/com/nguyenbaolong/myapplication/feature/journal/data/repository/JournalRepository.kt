package com.nguyenbaolong.myapplication.feature.journal.data.repository

import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogDao
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class JournalRepository(private val dao: ActivityLogDao) {
    fun getLogsForDate(date: LocalDate): Flow<List<ActivityLogEntity>> =
        dao.getLogsByDate(date.toEpochDay())

    fun getAllLogs(): Flow<List<ActivityLogEntity>> =
        dao.getAllLogs()

    fun getLogsBetweenDates(startDate: LocalDate, endDate: LocalDate): Flow<List<ActivityLogEntity>> =
        dao.getLogsBetweenDates(startDate.toEpochDay(), endDate.toEpochDay())

    suspend fun addLog(log: ActivityLogEntity): Long = dao.insert(log)

    suspend fun updateLog(log: ActivityLogEntity) = dao.update(log)

    suspend fun deleteLog(log: ActivityLogEntity) = dao.delete(log)

    suspend fun deleteAllLogs() = dao.deleteAll()
}

