package org.freedomtool.utils

import java.time.Duration
import java.time.Instant
import java.util.Date

fun daysBetween(endDate: Date): Int {
    val endInstant = Instant.ofEpochMilli(endDate.time)
    val currentInstant = Instant.now()
    val duration = Duration.between(currentInstant, endInstant)

    return duration.toDays().toInt()
}