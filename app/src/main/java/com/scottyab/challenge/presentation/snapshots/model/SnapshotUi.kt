package com.scottyab.challenge.presentation.snapshots.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Parcelize
data class SnapshotUi(
    val id: String,
    val title: String,
    val imageUrl: String,
    val recordedAt: LocalDateTime,
) : Parcelable {

    @IgnoredOnParcel
    val recordedAtFormatted: String =
        recordedAt.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
}

val EMPTY_SNAPSHOT =
    SnapshotUi(
        id = "",
        imageUrl = "",
        title = "",
        recordedAt = LocalDateTime.now(),
    )
