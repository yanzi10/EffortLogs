package com.nguyenbaolong.myapplication.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

object DateUtils {

    /**
     * Calculates the effective calendar date given a custom start-of-day hour (0..23).
     *
     * In productivity and habit apps:
     * - If startOfDayHour is 0 (00:00 / Midnight), each calendar date starts strictly at midnight.
     * - If startOfDayHour is e.g. 5 (05:00 AM), the hours between 00:00 and 04:59 belong to the previous day's cycle.
     * - If startOfDayHour is e.g. 23 (23:00 / 11 PM), hours 00:00 through 22:59 belong to the previous day's cycle.
     */
    fun getEffectiveDate(
        now: LocalDateTime = LocalDateTime.now(),
        startOfDayHour: Int = 0
    ): LocalDate {
        val clampedHour = startOfDayHour.coerceIn(0, 23)
        return if (clampedHour > 0 && now.hour < clampedHour) {
            now.toLocalDate().minusDays(1)
        } else {
            now.toLocalDate()
        }
    }

    /**
     * Formats an hour integer (0..23) into standard 24h & 12h representation:
     * e.g., 0 -> "00:00 (12 AM)", 5 -> "05:00 (5 AM)", 12 -> "12:00 (12 PM)", 23 -> "23:00 (11 PM)".
     */
    fun formatHour(hour: Int): String {
        val h = hour.coerceIn(0, 23)
        val period = if (h < 12) "AM" else "PM"
        val h12 = when (val mod = h % 12) {
            0 -> 12
            else -> mod
        }
        return String.format(Locale.US, "%02d:00 (%d %s)", h, h12, period)
    }

    /**
     * Formats an hour integer (0..23) into clean 24h string: e.g., 5 -> "05:00", 23 -> "23:00".
     */
    fun formatHour24(hour: Int): String {
        val h = hour.coerceIn(0, 23)
        return String.format(Locale.US, "%02d:00", h)
    }
}
