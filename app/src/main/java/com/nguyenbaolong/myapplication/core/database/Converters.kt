package com.nguyenbaolong.myapplication.core.database

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }
    @TypeConverter fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
    @TypeConverter fun fromEpochMilli(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }
    @TypeConverter fun toEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()
}
