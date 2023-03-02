package com.scottyab.challenge.data.datasource.local.db

import androidx.room.TypeConverter
import java.time.LocalDateTime

object LocalDateTimeConverter {

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? = if (dateString?.isNotBlank() == true) {
        LocalDateTime.parse(dateString)
    } else {
        null
    }

    @TypeConverter
    fun fromLocalDateTimeString(date: LocalDateTime?): String? = date?.toString()
}
