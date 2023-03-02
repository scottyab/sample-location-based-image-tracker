package com.scottyab.challenge.presentation.snapshots.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class SnapshotUi(
    val id: String,
    val title: String,
    val imageUrl: String,
    val recordedAt: LocalDateTime
) : Parcelable

val EMPTY_ARTICLE = SnapshotUi(
    id = "",
    imageUrl = "",
    title = "",
    recordedAt = LocalDateTime.now()
)
