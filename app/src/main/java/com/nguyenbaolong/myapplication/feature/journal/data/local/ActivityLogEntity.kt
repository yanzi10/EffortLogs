package com.nguyenbaolong.myapplication.feature.journal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawInput: String,
    val inputType: InputType = InputType.TEXT,
    val detectedAction: String,
    val category: ActivityCategory = ActivityCategory.OTHER,
    val durationMinutes: Int? = null,
    val activityDate: LocalDate,
    val timeOfDay: TimeOfDay? = null,
    val activityTimestamp: Instant? = null,
    val loggedAt: Instant = Instant.now()
)

