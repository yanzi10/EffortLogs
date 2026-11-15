package com.nguyenbaolong.myapplication.feature.journal.domain

import com.nguyenbaolong.myapplication.core.utils.TimeOfDay
import com.nguyenbaolong.myapplication.feature.journal.data.local.ActivityCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class NlpParserTest {

    private val fixedToday = LocalDate.of(2026, 11, 1)
    private val parser = NlpParser(clock = { fixedToday })

    data class TestCase(
        val input: String,
        val expectedCategory: ActivityCategory,
        val expectedDuration: Int?,
        val expectedDate: LocalDate,
        val expectedTimeOfDay: TimeOfDay?
    )

    private val testCases = listOf(
        // Study tests
        TestCase("Studied math for 2 hours this morning", ActivityCategory.STUDY, 120, fixedToday, TimeOfDay.MORNING),
        TestCase("Read chapter 4 of physics book for 45 minutes", ActivityCategory.STUDY, 45, fixedToday, null),
        TestCase("Completed homework assignment yesterday afternoon", ActivityCategory.STUDY, null, fixedToday.minusDays(1), TimeOfDay.AFTERNOON),
        TestCase("Coding Android project for 1.5 hours tonight", ActivityCategory.STUDY, 90, fixedToday, TimeOfDay.EVENING),
        TestCase("Attended machine learning lecture for one hour", ActivityCategory.STUDY, 60, fixedToday, null),
        TestCase("Studied for biology exam for 30 mins", ActivityCategory.STUDY, 30, fixedToday, null),
        TestCase("Reviewing chemistry revision notes for half an hour", ActivityCategory.STUDY, 30, fixedToday, null),
        TestCase("Programming algorithm homework for two hours", ActivityCategory.STUDY, 120, fixedToday, null),

        // Fitness tests
        TestCase("Gym workout for 1 hour this morning", ActivityCategory.FITNESS, 60, fixedToday, TimeOfDay.MORNING),
        TestCase("Ran 5km in the park for 30 minutes", ActivityCategory.FITNESS, 30, fixedToday, null),
        TestCase("Yoga and stretching session for 45 mins this evening", ActivityCategory.FITNESS, 45, fixedToday, TimeOfDay.EVENING),
        TestCase("Swimming at the pool for one hour yesterday", ActivityCategory.FITNESS, 60, fixedToday.minusDays(1), null),
        TestCase("Cardio exercise for twenty minutes", ActivityCategory.FITNESS, 20, fixedToday, null),
        TestCase("Cycling around the lake for 1 hour 30 minutes", ActivityCategory.FITNESS, 90, fixedToday, null),
        TestCase("Did 100 pushups and pullups this afternoon", ActivityCategory.FITNESS, null, fixedToday, TimeOfDay.AFTERNOON),
        TestCase("Heavy weight lifting at the gym for 50 minutes", ActivityCategory.FITNESS, 50, fixedToday, null),

        // Work tests
        TestCase("Team standup meeting for 15 minutes this morning", ActivityCategory.WORK, 15, fixedToday, TimeOfDay.MORNING),
        TestCase("Client presentation preparation for 2 hours", ActivityCategory.WORK, 120, fixedToday, null),
        TestCase("Replied to client emails for 30 mins", ActivityCategory.WORK, 30, fixedToday, null),
        TestCase("Fixed critical production bug for 1.5 hours tonight", ActivityCategory.WORK, 90, fixedToday, TimeOfDay.EVENING),
        TestCase("Quarterly project planning meeting for one hour", ActivityCategory.WORK, 60, fixedToday, null),
        TestCase("Reviewed pull requests for 40 minutes this afternoon", ActivityCategory.WORK, 40, fixedToday, TimeOfDay.AFTERNOON),
        TestCase("Deployed backend service to staging yesterday", ActivityCategory.WORK, null, fixedToday.minusDays(1), null),
        TestCase("Written weekly progress report for 25 minutes", ActivityCategory.WORK, 25, fixedToday, null),

        // Personal tests
        TestCase("Morning meditation for 15 minutes this morning", ActivityCategory.PERSONAL, 15, fixedToday, TimeOfDay.MORNING),
        TestCase("Cooked dinner for family for 45 minutes tonight", ActivityCategory.PERSONAL, 45, fixedToday, TimeOfDay.EVENING),
        TestCase("Grocery shopping at supermarket for one hour", ActivityCategory.PERSONAL, 60, fixedToday, null),
        TestCase("Cleaning the apartment for two hours yesterday", ActivityCategory.PERSONAL, 120, fixedToday.minusDays(1), null),
        TestCase("Relaxing and listening to music for half an hour", ActivityCategory.PERSONAL, 30, fixedToday, null),
        TestCase("Watched a movie with friends for 2 hours tonight", ActivityCategory.PERSONAL, 120, fixedToday, TimeOfDay.EVENING),
        TestCase("Called mom and talked for thirty minutes", ActivityCategory.PERSONAL, 30, fixedToday, null),
        TestCase("Laundry and washing dishes for 40 mins this afternoon", ActivityCategory.PERSONAL, 40, fixedToday, TimeOfDay.AFTERNOON),

        // Other / Edge case tests
        TestCase("Walking downtown for 20 minutes", ActivityCategory.FITNESS, 20, fixedToday, null),
        TestCase("Just rested for an hour", ActivityCategory.OTHER, 60, fixedToday, null),
        TestCase("Quick break for ten minutes", ActivityCategory.OTHER, 10, fixedToday, null),
        TestCase("Organized desk and cleaned room for 30 minutes", ActivityCategory.PERSONAL, 30, fixedToday, null),
        TestCase("Finished reading technical book yesterday evening", ActivityCategory.STUDY, null, fixedToday.minusDays(1), TimeOfDay.EVENING),
        TestCase("Attended dentist appointment for 45 mins", ActivityCategory.PERSONAL, 45, fixedToday, null),
        TestCase("Office team sync for 30 minutes this morning", ActivityCategory.WORK, 30, fixedToday, TimeOfDay.MORNING),
        TestCase("Played badminton match for 1 hour yesterday afternoon", ActivityCategory.FITNESS, 60, fixedToday.minusDays(1), TimeOfDay.AFTERNOON),
        TestCase("Studied history for 3 hours", ActivityCategory.STUDY, 180, fixedToday, null),
        TestCase("Went for a morning jog for 25 minutes", ActivityCategory.FITNESS, 25, fixedToday, TimeOfDay.MORNING)
    )

    @Test
    fun testAccuracyExceeds80Percent() {
        var correctCategory = 0
        var correctDuration = 0
        var correctDate = 0
        var correctTimeOfDay = 0
        val total = testCases.size

        for (tc in testCases) {
            val result = parser.parse(tc.input)

            if (result.category == tc.expectedCategory) correctCategory++
            if (result.durationMinutes == tc.expectedDuration) correctDuration++
            if (result.activityDate == tc.expectedDate) correctDate++
            if (result.timeOfDay == tc.expectedTimeOfDay) correctTimeOfDay++
        }

        val categoryAcc = (correctCategory.toDouble() / total) * 100
        val durationAcc = (correctDuration.toDouble() / total) * 100
        val dateAcc = (correctDate.toDouble() / total) * 100
        val timeOfDayAcc = (correctTimeOfDay.toDouble() / total) * 100

        assertTrue("Category accuracy was $categoryAcc% (expected >= 80%)", categoryAcc >= 80.0)
        assertTrue("Duration accuracy was $durationAcc% (expected >= 80%)", durationAcc >= 80.0)
        assertTrue("Date accuracy was $dateAcc% (expected >= 80%)", dateAcc >= 80.0)
        assertTrue("Time of day accuracy was $timeOfDayAcc% (expected >= 80%)", timeOfDayAcc >= 80.0)
    }

    @Test
    fun testEmptyInputHandledGracefully() {
        val result = parser.parse("   ")
        assertEquals(fixedToday, result.activityDate)
        assertEquals(ActivityCategory.OTHER, result.category)
        assertEquals(null, result.durationMinutes)
    }

    @Test
    fun testTargetDatePreservedForPastDate() {
        val pastDate = LocalDate.of(2026, 10, 15)
        // 1. Without explicit relative date: must preserve past target date
        val result1 = parser.parse("Studied math for 2 hours", baseDate = pastDate)
        assertEquals(pastDate, result1.activityDate)
        assertEquals(120, result1.durationMinutes)
        assertEquals(ActivityCategory.STUDY, result1.category)

        // 2. With 'yesterday': must compute relative to past target date
        val result2 = parser.parse("Gym workout for 45 minutes yesterday", baseDate = pastDate)
        assertEquals(pastDate.minusDays(1), result2.activityDate)
        assertEquals(45, result2.durationMinutes)
        assertEquals(ActivityCategory.FITNESS, result2.category)
    }
}

