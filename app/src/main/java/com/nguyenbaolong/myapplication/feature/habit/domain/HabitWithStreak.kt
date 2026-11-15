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

fun calculateStreaks(dates: List<LocalDate>, today: LocalDate = LocalDate.now()): Pair<Int, Int> {
    if (dates.isEmpty()) return 0 to 0
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
