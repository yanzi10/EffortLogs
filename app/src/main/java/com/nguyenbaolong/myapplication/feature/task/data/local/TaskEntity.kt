package com.nguyenbaolong.myapplication.feature.task.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import java.time.LocalDate

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timeOfDay: TimeOfDay? = null,
    val isCompleted: Boolean = false,
    val taskDate: LocalDate
)
