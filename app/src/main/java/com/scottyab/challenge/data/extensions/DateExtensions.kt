package com.scottyab.challenge.data.extensions

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun nowUTC(): LocalDateTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()
