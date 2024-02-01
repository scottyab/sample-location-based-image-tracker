package com.scottyab.challenge.domain

import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.Snapshot
import kotlinx.coroutines.flow.Flow

interface SnapshotRepository {
    fun getSnapshots(activityId: String): Flow<List<Snapshot>>

    suspend fun addSnapshot(
        activityId: String,
        location: Location,
    )

    suspend fun getSnapShot(snapshotId: String): Snapshot

    /** TODO remove this as it's not needed as we deal with this on the activity level **/
    suspend fun reset(activityId: String)
}
