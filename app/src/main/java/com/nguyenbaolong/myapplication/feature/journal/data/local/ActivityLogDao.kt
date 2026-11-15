package com.nguyenbaolong.myapplication.feature.journal.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs WHERE activityDate = :epochDay ORDER BY loggedAt DESC")
    fun getLogsByDate(epochDay: Long): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs ORDER BY activityDate DESC, loggedAt DESC")
    fun getAllLogs(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE activityDate BETWEEN :startEpochDay AND :endEpochDay ORDER BY activityDate DESC, loggedAt DESC")
    fun getLogsBetweenDates(startEpochDay: Long, endEpochDay: Long): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: ActivityLogEntity): Long

    @Update
    suspend fun update(log: ActivityLogEntity)

    @Delete
    suspend fun delete(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs")
    suspend fun deleteAll()
}

