package com.nguyenbaolong.myapplication.feature.habit.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculationTest {

    private val fixedToday = LocalDate.of(2026, 11, 1)

    @Test
    fun emptyListReturnsZeroStreak() {
        val (current, best) = calculateStreaks(emptyList(), fixedToday)
        assertEquals(0, current)
        assertEquals(0, best)
    }

    @Test
    fun completedTodayGivesStreakOne() {
        val dates = listOf(fixedToday)
        val (current, best) = calculateStreaks(dates, fixedToday)
        assertEquals(1, current)
        assertEquals(1, best)
    }

    @Test
    fun completedYesterdayGivesStreakOne() {
        val dates = listOf(fixedToday.minusDays(1))
        val (current, best) = calculateStreaks(dates, fixedToday)
        assertEquals(1, current)
        assertEquals(1, best)
    }

    @Test
    fun threeConsecutiveDaysEndingTodayGivesStreakThree() {
        val dates = listOf(
            fixedToday,
            fixedToday.minusDays(1),
            fixedToday.minusDays(2)
        )
        val (current, best) = calculateStreaks(dates, fixedToday)
        assertEquals(3, current)
        assertEquals(3, best)
    }

    @Test
    fun missedDayResetsCurrentStreakToZero() {
        // Completed 2 days ago, but neither yesterday nor today
        val dates = listOf(
            fixedToday.minusDays(2),
            fixedToday.minusDays(3),
            fixedToday.minusDays(4)
        )
        val (current, best) = calculateStreaks(dates, fixedToday)
        assertEquals(0, current)
        assertEquals(3, best)
    }

    @Test
    fun bestStreakPreservedWhenCurrentIsSmaller() {
        // Chain of 5 in the past, then gap, then 2 recent
        val dates = listOf(
            fixedToday,
            fixedToday.minusDays(1),
            // Gap at minusDays(2)
            fixedToday.minusDays(10),
            fixedToday.minusDays(11),
            fixedToday.minusDays(12),
            fixedToday.minusDays(13),
            fixedToday.minusDays(14)
        )
        val (current, best) = calculateStreaks(dates, fixedToday)
        assertEquals(2, current)
        assertEquals(5, best)
    }
}

