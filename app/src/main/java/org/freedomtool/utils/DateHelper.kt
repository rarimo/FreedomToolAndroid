package org.freedomtool.utils

import android.content.Context
import org.freedomtool.R
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getDaysAndHoursBetween(endDate: Long): Pair<Int, Int> {
    val date = Date(endDate * 1000)
    val endInstant = Instant.ofEpochMilli(date.time)
    val currentInstant = Instant.now()
    val duration = Duration.between(currentInstant, endInstant)

    val days = duration.toDays().toInt()
    val hours = (duration.toHours() % 24).toInt()
    return Pair(days, hours)
}

fun isEnded(endDate: Long): Boolean {
    val (days, hours) = getDaysAndHoursBetween(endDate)

    return days <= 0 && hours <= 0
}

fun resolveDays(context: Context, endDate: Long): String {
    val (days, hours) = getDaysAndHoursBetween(endDate)

    return when {
        days > 0 && hours > 0 -> context.getString(
            R.string.ends_in_x_days_y_hours,
            days.toString(),
            hours.toString()
        )

        days > 0 -> context.getString(R.string.ends_in_x_days, days.toString())
        hours > 0 -> context.getString(R.string.starts_in_x_hours, hours.toString())
        else -> context.getString(R.string.completed_vote)
    }
}

fun calculateAge(birthdate: String): Int {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    try {
        val birthDate = dateFormat.parse(birthdate)
        val currentDate = Calendar.getInstance().time

        val calendarBirth = Calendar.getInstance()
        calendarBirth.time = birthDate!!

        val calendarCurrent = Calendar.getInstance()
        calendarCurrent.time = currentDate

        var age = calendarCurrent.get(Calendar.YEAR) - calendarBirth.get(Calendar.YEAR)

        if (calendarBirth.get(Calendar.DAY_OF_YEAR) > calendarCurrent.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    } catch (e: Exception) {

        return -1
    }
}