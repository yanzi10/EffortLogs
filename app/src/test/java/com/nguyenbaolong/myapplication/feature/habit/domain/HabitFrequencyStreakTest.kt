package com.nguyenbaolong.myapplication.feature.habit.domain

import com.nguyenbaolong.myapplication.feature.habit.data.local.HabitEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class HabitFrequencyStreakTest {

    // 2026-11-06 is a Friday (DayOfWeek = 5)
    // 2026-11-04 is a Wednesday (DayOfWeek = 3)
    // 2026-11-02 is a Monday (DayOfWeek = 1)
    private val friday = LocalDate.of(2026, 11, 6)
    private val wednesday = LocalDate.of(2026, 11, 4)
    private val monday = LocalDate.of(2026, 11, 2)

    @Test
    fun testSpecificDaysStreakMaintainedAcrossRestDays() {
        val habit = HabitEntity(
            id = 1,
            name = "Gym Workout",
            frequencyType = "SPECIFIC_DAYS",
            targetDays = "1,3,5" // Mon, Wed, Fri
        )

        // User completed Monday, Wednesday, Friday
        val completedDates = listOf(monday, wednesday, friday)

        val (currentStreak, bestStreak) = calculateStreaks(
            dates = completedDates,
            today = friday,
            habit = habit
        )

        // Tuesday and Thursday were rest days, so streak should be 3!
        assertEquals(3, currentStreak)
        assertEquals(3, bestStreak)
    }

    @Test
    fun testSpecificDaysStreakBreaksWhenScheduledDayMissed() {
        val habit = HabitEntity(
            id = 1,
            name = "Running",
            frequencyType = "SPECIFIC_DAYS",
            targetDays = "1,3,5" // Mon, Wed, Fri
        )

        // User completed Monday and Friday, but MISSED Wednesday!
        val completedDates = listOf(monday, friday)

        val (currentStreak, bestStreak) = calculateStreaks(
            dates = completedDates,
            today = friday,
            habit = habit
        )

        // Missing Wednesday broke the streak, so current streak on Friday is 1
        assertEquals(1, currentStreak)
        assertEquals(1, bestStreak)
    }

    @Test
    fun testSpecificDaysStreakOnNonScheduledDay() {
        val habit = HabitEntity(
            id = 1,
            name = "Swimming",
            frequencyType = "SPECIFIC_DAYS",
            targetDays = "1,3,5" // Mon, Wed, Fri
        )

        // User completed Mon, Wed, Fri. Today is Saturday (Nov 7)
        val saturday = LocalDate.of(2026, 11, 7)
        val completedDates = listOf(monday, wednesday, friday)

        val (currentStreak, bestStreak) = calculateStreaks(
            dates = completedDates,
            today = saturday,
            habit = habit
        )

        // On Saturday (non-scheduled day), the streak from Friday is preserved
        assertEquals(3, currentStreak)
        assertEquals(3, bestStreak)
    }
}
