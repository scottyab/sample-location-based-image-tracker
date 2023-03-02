package com.scottyab.challenge.presentation.snapshots

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.scottyab.challenge.R
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.SnapshotTracker
import com.scottyab.challenge.domain.model.TrackingState
import com.scottyab.challenge.domain.model.TrackingState.Started
import com.scottyab.challenge.domain.model.TrackingState.Stopped
import com.scottyab.challenge.presentation.common.BaseViewModel
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import timber.log.Timber

class SnapshotsViewModel(
    private val snapshotRepository: SnapshotRepository,
    private val snapshotUiMapper: SnapshotUiMapper,
    private val snapshotTracker: SnapshotTracker,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<SnapshotsState>(SnapshotsState()) {

    private var snapshotsObserverJob: Job? = null

    init {
        setState { SnapshotsState() }
        // listen for tracking state changes in the tracker
        viewModelScope.launch {
            snapshotTracker.state
                .flowOn(backgroundDispatcher)
                .collect { newTrackingState ->
                    when (newTrackingState) {
                        is Started -> {
                            // start listening for snapshot updates
                            observeSnapshots(newTrackingState.activityId)
                            setState {
                                copy(
                                    activityId = newTrackingState.activityId,
                                    trackingState = newTrackingState
                                )
                            }
                        }
                        is Stopped -> {
                            setState {
                                copy(
                                    trackingState = newTrackingState
                                )
                            }
                        }
                    }
                }
        }
    }

    @TestOnly
    internal fun observeSnapshots(activityId: String) {
        if (snapshotsObserverJob?.isActive == true) {
            snapshotsObserverJob?.cancel()
        }
        // listen for location changes in the repo
        snapshotsObserverJob = viewModelScope.launch {
            snapshotRepository.getSnapshots(activityId)
                .flowOn(backgroundDispatcher)
                .map { snapshots ->
                    setState { copy(snapshots = snapshotUiMapper.toUi(snapshots)) }
                }.collect()
        }
    }

    fun loadSnapshots(activityId: String) {
        observeSnapshots(activityId)
    }

    fun trackingClick() {
        Timber.d("tracking Clicked")
        when (state.value?.trackingState) {
            is Started -> snapshotTracker.stop()
            is Stopped -> snapshotTracker.start()
            else -> error("Invalid state")
        }
    }

    fun resetClick() {
        viewModelScope.launch {
            state.value?.activityId?.let {
                snapshotRepository.reset(it)
            }
        }
    }
}

data class SnapshotsState(
    val trackingState: TrackingState = Stopped,
    val snapshots: List<SnapshotUi> = emptyList(),
    val activityId: String? = null
) {
    @StringRes val toggleButtonTextResId = when (trackingState) {
        is Started -> R.string.snapshot_stop_tracking
        is Stopped -> R.string.snapshot_start_tracking
    }
}
