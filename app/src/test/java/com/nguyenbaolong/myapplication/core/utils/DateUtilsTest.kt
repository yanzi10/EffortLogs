package com.nguyenbaolong.myapplication.core.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class DateUtilsTest {

    private val baseDate = LocalDate.of(2026, 11, 2)

    @Test
    fun testStartOfDayHourZeroMidnight() {
        // At hour 0 (00:00 start of day), any time of day returns the exact calendar day
        val midnight = LocalDateTime.of(2026, 11, 2, 0, 0)
        val noon = LocalDateTime.of(2026, 11, 2, 12, 0)
        val night = LocalDateTime.of(2026, 11, 2, 23, 59)

        assertEquals(baseDate, DateUtils.getEffectiveDate(midnight, 0))
        assertEquals(baseDate, DateUtils.getEffectiveDate(noon, 0))
        assertEquals(baseDate, DateUtils.getEffectiveDate(night, 0))
    }

    @Test
    fun testStartOfDayHourFiveEarlyMorning() {
        // Start of day is 5 AM. Before 5 AM (e.g. 03:00, 04:59) should count as previous day (Nov 1)
        val earlyMorning = LocalDateTime.of(2026, 11, 2, 3, 30)
        val rightBeforeFive = LocalDateTime.of(2026, 11, 2, 4, 59)
        val exactFive = LocalDateTime.of(2026, 11, 2, 5, 0)
        val afternoon = LocalDateTime.of(2026, 11, 2, 14, 0)

        assertEquals(baseDate.minusDays(1), DateUtils.getEffectiveDate(earlyMorning, 5))
        assertEquals(baseDate.minusDays(1), DateUtils.getEffectiveDate(rightBeforeFive, 5))
        assertEquals(baseDate, DateUtils.getEffectiveDate(exactFive, 5))
        assertEquals(baseDate, DateUtils.getEffectiveDate(afternoon, 5))
    }

    @Test
    fun testStartOfDayHourTwentyThreeNight() {
        // Start of day is 23:00 (11 PM). All hours before 23:00 (e.g. 10:00, 22:59) count as previous day
        val morning = LocalDateTime.of(2026, 11, 2, 10, 0)
        val lateEvening = LocalDateTime.of(2026, 11, 2, 22, 59)
        val exactTwentyThree = LocalDateTime.of(2026, 11, 2, 23, 0)
        val endOfDay = LocalDateTime.of(2026, 11, 2, 23, 59)

        assertEquals(baseDate.minusDays(1), DateUtils.getEffectiveDate(morning, 23))
        assertEquals(baseDate.minusDays(1), DateUtils.getEffectiveDate(lateEvening, 23))
        assertEquals(baseDate, DateUtils.getEffectiveDate(exactTwentyThree, 23))
        assertEquals(baseDate, DateUtils.getEffectiveDate(endOfDay, 23))
    }

    @Test
    fun testFormatHourDisplaysAllHoursCorrectly() {
        assertEquals("00:00 (12 AM)", DateUtils.formatHour(0))
        assertEquals("05:00 (5 AM)", DateUtils.formatHour(5))
        assertEquals("12:00 (12 PM)", DateUtils.formatHour(12))
        assertEquals("18:00 (6 PM)", DateUtils.formatHour(18))
        assertEquals("23:00 (11 PM)", DateUtils.formatHour(23))
    }
}
