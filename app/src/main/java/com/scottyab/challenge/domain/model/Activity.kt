package com.scottyab.challenge.domain.model

import java.time.LocalDateTime

data class Activity(
    val id: String,
    val title: String,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime?
)
