package com.nguyenbaolong.myapplication.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitCompletionEntity
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitDao
import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogDao
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityLogEntity
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskDao
import com.nguyenbaolong.myapplication.feature.task.data.local.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        ActivityLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun habitDao(): HabitDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `activity_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `rawInput` TEXT NOT NULL,
                        `inputType` TEXT NOT NULL,
                        `detectedAction` TEXT NOT NULL,
                        `category` TEXT NOT NULL,
                        `durationMinutes` INTEGER,
                        `activityDate` INTEGER NOT NULL,
                        `timeOfDay` TEXT,
                        `activityTimestamp` INTEGER,
                        `loggedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `habits` ADD COLUMN `frequencyType` TEXT NOT NULL DEFAULT 'DAILY'")
                db.execSQL("ALTER TABLE `habits` ADD COLUMN `targetDays` TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "effort_log.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
                .also { INSTANCE = it }
            }
    }
}
