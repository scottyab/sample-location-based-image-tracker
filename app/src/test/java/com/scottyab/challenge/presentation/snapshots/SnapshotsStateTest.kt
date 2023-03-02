package com.scottyab.challenge.presentation.snapshots

import com.scottyab.challenge.R
import com.scottyab.challenge.domain.model.TrackingState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SnapshotsStateTest {

    @Test
    fun `getToggleButtonTextResId SHOULD return start res Id WHEN Stopped`() {
        val sut = SnapshotsState(trackingState = TrackingState.Stopped)

        assertThat(sut.toggleButtonTextResId).isEqualTo(R.string.snapshot_start_tracking)
    }

    @Test
    fun `getToggleButtonTextResId SHOULD return stop res Id WHEN Started`() {
        val sut = SnapshotsState(trackingState = TrackingState.Started(activityId = "foo"))

        assertThat(sut.toggleButtonTextResId).isEqualTo(R.string.snapshot_stop_tracking)
    }
}
