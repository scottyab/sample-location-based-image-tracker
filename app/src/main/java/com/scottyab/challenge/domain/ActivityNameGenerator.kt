package com.scottyab.challenge.domain

import com.scottyab.challenge.data.extensions.nowUTC
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

interface ActivityNameGenerator {
    fun generate(): String
}

class TimeOfDayActivityNameGenerator : ActivityNameGenerator {

    // TODO In real app I'd use AndroidResources for the strings
    override fun generate(): String {
        val now = nowUTC()
        return when (now.toLocalTime().hour) {
            in 5..12 -> "Morning"
            in 13..17 -> "Afternoon"
            in 18..21 -> "Evening"
            else -> "Night"
        }.plus(" activity on ${ISO_LOCAL_DATE.format(now)}")
    }
}
