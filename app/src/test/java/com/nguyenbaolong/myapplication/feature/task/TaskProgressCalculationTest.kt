package com.nguyenbaolong.myapplication.feature.task

import org.junit.Assert.assertEquals
import org.junit.Test

class TaskProgressCalculationTest {

    private fun calculateCompletionPercentage(completed: Int, total: Int): Int {
        if (total <= 0) return 0
        return (completed * 100) / total
    }

    @Test
    fun zeroTasksReturnsZeroPercentageWithoutDivisionByZero() {
        val percentage = calculateCompletionPercentage(completed = 0, total = 0)
        assertEquals(0, percentage)
    }

    @Test
    fun oneOfThreeReturnsThirtyThreePercent() {
        val percentage = calculateCompletionPercentage(completed = 1, total = 3)
        assertEquals(33, percentage)
    }

    @Test
    fun twoOfThreeReturnsSixtySixPercent() {
        val percentage = calculateCompletionPercentage(completed = 2, total = 3)
        assertEquals(66, percentage)
    }

    @Test
    fun allTasksCompletedReturnsOneHundredPercent() {
        val percentage = calculateCompletionPercentage(completed = 5, total = 5)
        assertEquals(100, percentage)
    }
}

