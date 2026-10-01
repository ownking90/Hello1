package com.smartmate.ai.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateTimeUtilsTest {

    @Test
    fun `isToday returns true for current time`() {
        assertTrue(DateTimeUtils.isToday(System.currentTimeMillis()))
    }

    @Test
    fun `isToday returns false for a different day`() {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        assertFalse(DateTimeUtils.isToday(yesterday))
    }

    @Test
    fun `isUpcoming is false for today and true for future days`() {
        val now = System.currentTimeMillis()
        assertFalse(DateTimeUtils.isUpcoming(now))

        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.timeInMillis
        assertTrue(DateTimeUtils.isUpcoming(tomorrow))
    }

    @Test
    fun `isUpcoming is false for a past time`() {
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis
        assertFalse(DateTimeUtils.isUpcoming(yesterday))
    }

    @Test
    fun `greetingForHour maps hours to the expected greeting`() {
        assertEquals(DateTimeUtils.Greeting.MORNING, DateTimeUtils.greetingForHour(8))
        assertEquals(DateTimeUtils.Greeting.AFTERNOON, DateTimeUtils.greetingForHour(14))
        assertEquals(DateTimeUtils.Greeting.EVENING, DateTimeUtils.greetingForHour(20))
        assertEquals(DateTimeUtils.Greeting.EVENING, DateTimeUtils.greetingForHour(2))
    }

    @Test
    fun `isSameDay compares calendar day not just 24h window`() {
        val cal = Calendar.getInstance()
        cal.set(2024, Calendar.JANUARY, 10, 23, 0)
        val lateNight = cal.timeInMillis
        cal.set(2024, Calendar.JANUARY, 11, 1, 0)
        val earlyNextDay = cal.timeInMillis

        assertFalse(DateTimeUtils.isSameDay(lateNight, earlyNextDay))
    }
}
