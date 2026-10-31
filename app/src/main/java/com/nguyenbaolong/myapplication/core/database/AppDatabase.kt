package com.nguyenbaolong.myapplication.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitCompletionEntity
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitDao
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskDao
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity

@Database(
    entities = [TaskEntity::class, HabitEntity::class, HabitCompletionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "effort_log.db"
                ).build().also { INSTANCE = it }
            }
    }
}
