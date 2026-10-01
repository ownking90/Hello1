package com.smartmate.ai.notifications

import com.smartmate.ai.domain.RepeatOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSchedulerTest {

    private val oneDayMillis = 24L * 60L * 60L * 1000L

    @Test
    fun `once reminders do not advance`() {
        val now = 1_000_000L
        assertEquals(now, ReminderScheduler.nextTriggerTime(now, RepeatOption.ONCE))
    }

    @Test
    fun `daily reminders advance by one day`() {
        val now = 1_000_000L
        assertEquals(now + oneDayMillis, ReminderScheduler.nextTriggerTime(now, RepeatOption.DAILY))
    }

    @Test
    fun `weekly reminders advance by seven days`() {
        val now = 1_000_000L
        assertEquals(now + oneDayMillis * 7, ReminderScheduler.nextTriggerTime(now, RepeatOption.WEEKLY))
    }

    @Test
    fun `monthly reminders advance by thirty days`() {
        val now = 1_000_000L
        assertEquals(now + oneDayMillis * 30, ReminderScheduler.nextTriggerTime(now, RepeatOption.MONTHLY))
    }
}
