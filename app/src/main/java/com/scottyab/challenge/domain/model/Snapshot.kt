package com.scottyab.challenge.domain.model

import java.time.LocalDateTime

data class Snapshot(
    val id: String,
    val activityId: String,
    val imageUrl: String,
    val title: String,
    val recordedAt: LocalDateTime,
    val location: Location
)
