package com.nguyenbaolong.myapplication.feature.habit.domain

import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import java.time.LocalDate

data class HabitWithStreak(
    val habit: HabitEntity,
    val completedToday: Boolean,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionDates: List<LocalDate>
)

fun calculateStreaks(
    dates: List<LocalDate>,
    today: LocalDate = LocalDate.now(),
    habit: HabitEntity? = null
): Pair<Int, Int> {
    if (dates.isEmpty()) return 0 to 0
    val completionSet = dates.toSet()

    // Parse scheduled days if SPECIFIC_DAYS
    val scheduledDaysOfWeek: Set<Int>? = if (habit?.frequencyType == "SPECIFIC_DAYS" && habit.targetDays.isNotBlank()) {
        habit.targetDays.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet().takeIf { it.isNotEmpty() }
    } else null

    // Helper: is date a scheduled day for this habit?
    fun isScheduledDate(d: LocalDate): Boolean {
        if (scheduledDaysOfWeek == null) return true
        return scheduledDaysOfWeek.contains(d.dayOfWeek.value)
    }

    if (scheduledDaysOfWeek == null) {
        val sorted = dates.distinct().sortedDescending()
        val yesterday = today.minusDays(1)

        // currentStreak: consecutive chain ending today or yesterday
        var current = 0
        var cursor = if (sorted.first() == today) today else if (sorted.first() == yesterday) yesterday else null
        if (cursor != null) {
            for (date in sorted) {
                if (date == cursor) {
                    current++
                    cursor = cursor.minusDays(1)
                } else if (date.isBefore(cursor)) {
                    break
                }
            }
        }

        // bestStreak: max consecutive chain
        var best = 1
        var streak = 1
        for (i in 1 until sorted.size) {
            if (sorted[i] == sorted[i - 1].minusDays(1)) {
                streak++
            } else {
                if (streak > best) best = streak
                streak = 1
            }
        }
        if (streak > best) best = streak

        return current to best
    }

    // For SPECIFIC_DAYS: check backwards across scheduled days
    var lastScheduledDate = today
    while (!isScheduledDate(lastScheduledDate)) {
        lastScheduledDate = lastScheduledDate.minusDays(1)
    }

    val startCheckDate: LocalDate? = if (isScheduledDate(today)) {
        if (completionSet.contains(today)) {
            today
        } else {
            var prev = today.minusDays(1)
            while (!isScheduledDate(prev)) {
                prev = prev.minusDays(1)
            }
            if (completionSet.contains(prev)) prev else null
        }
    } else {
        if (completionSet.contains(lastScheduledDate)) lastScheduledDate else null
    }

    var currentStreak = 0
    if (startCheckDate != null) {
        var cursor: LocalDate = startCheckDate
        while (completionSet.contains(cursor)) {
            currentStreak++
            var prevDay = cursor.minusDays(1)
            while (!isScheduledDate(prevDay)) {
                prevDay = prevDay.minusDays(1)
            }
            cursor = prevDay
        }
    }

    var bestStreak = currentStreak
    val sortedAsc = dates.distinct().filter { isScheduledDate(it) }.sorted()
    if (sortedAsc.isNotEmpty()) {
        var running = 0
        for (i in sortedAsc.indices) {
            if (i == 0) {
                running = 1
            } else {
                var nextSched = sortedAsc[i - 1].plusDays(1)
                while (!isScheduledDate(nextSched)) {
                    nextSched = nextSched.plusDays(1)
                }
                if (sortedAsc[i] == nextSched) {
                    running++
                } else {
                    if (running > bestStreak) bestStreak = running
                    running = 1
                }
            }
            if (running > bestStreak) bestStreak = running
        }
    }

    return currentStreak to maxOf(currentStreak, bestStreak)
}
