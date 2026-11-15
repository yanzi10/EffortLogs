package com.nguyenbaolong.myapplication.feature.habit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cue: String? = null,
    val targetCompletionsPerWeek: Int = 7,
    val frequencyType: String = "DAILY",
    val targetDays: String = ""
)
