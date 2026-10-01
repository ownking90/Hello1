package com.smartmate.ai.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {

    fun formatDate(epochMillis: Long, pattern: String = "dd MMM yyyy"): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(epochMillis)

    fun formatTime(epochMillis: Long, pattern: String = "hh:mm a"): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(epochMillis)

    fun formatDateTime(epochMillis: Long): String = "${formatDate(epochMillis)} • ${formatTime(epochMillis)}"

    fun isToday(epochMillis: Long): Boolean = isSameDay(epochMillis, System.currentTimeMillis())

    fun isSameDay(a: Long, b: Long): Boolean {
        val calA = Calendar.getInstance().apply { timeInMillis = a }
        val calB = Calendar.getInstance().apply { timeInMillis = b }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
    }

    fun isUpcoming(epochMillis: Long): Boolean = epochMillis > System.currentTimeMillis() && !isToday(epochMillis)

    fun greetingForHour(hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): Greeting = when (hourOfDay) {
        in 4..11 -> Greeting.MORNING
        in 12..16 -> Greeting.AFTERNOON
        else -> Greeting.EVENING
    }

    enum class Greeting { MORNING, AFTERNOON, EVENING }
}
