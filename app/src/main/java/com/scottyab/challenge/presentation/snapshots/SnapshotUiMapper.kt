package com.scottyab.challenge.presentation.snapshots

import com.scottyab.challenge.domain.model.Snapshot
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi

class SnapshotUiMapper {

    fun toUi(snapshot: Snapshot) = SnapshotUi(
        id = snapshot.id,
        imageUrl = snapshot.imageUrl,
        recordedAt = snapshot.recordedAt,
        title = snapshot.title
    )

    fun toUi(snapshots: List<Snapshot>) = snapshots.map { toUi(it) }
}
