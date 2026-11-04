package com.nguyenbaolong.myapplication.core.database

import androidx.room.TypeConverter
import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import com.nguyenbaolong.myapplication.feature.journal.data.local.InputType
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromEpochMilli(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun toEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromTimeOfDay(value: TimeOfDay?): String? = value?.name

    @TypeConverter
    fun toTimeOfDay(value: String?): TimeOfDay? = value?.let {
        try {
            TimeOfDay.valueOf(it)
        } catch (_: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromCategory(value: ActivityCategory?): String? = value?.name

    @TypeConverter
    fun toCategory(value: String?): ActivityCategory? = value?.let {
        try {
            ActivityCategory.valueOf(it)
        } catch (_: Exception) {
            ActivityCategory.OTHER
        }
    }

    @TypeConverter
    fun fromInputType(value: InputType?): String? = value?.name

    @TypeConverter
    fun toInputType(value: String?): InputType? = value?.let {
        try {
            InputType.valueOf(it)
        } catch (_: Exception) {
            InputType.TEXT
        }
    }
}
