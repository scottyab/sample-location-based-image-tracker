package com.scottyab.challenge.domain

import com.scottyab.challenge.data.datasource.location.LocationProvider
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.TrackingState
import com.scottyab.challenge.domain.usecase.NewLocationUsecase
import com.scottyab.challenge.domain.usecase.NewLocationUsecaseResult
import com.scottyab.challenge.domain.usecase.StartActivityUsecase
import com.scottyab.challenge.domain.usecase.StartActivityUsecaseResult
import com.scottyab.challenge.presentation.common.AppCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import timber.log.Timber

/**
 * Central place to control the snapshot tracking and observe current state
 */
class SnapshotTracker(
    private val locationProvider: LocationProvider,
    private val newLocationUsecase: NewLocationUsecase,
    private val startActivityUsecase: StartActivityUsecase,
    private val appCoroutineScope: AppCoroutineScope
) {
    private var job: Job? = null

    @TestOnly
    var previousLocation: Location? = null

    // replay=1 will resend the latest item to new subscribers
    private val internalFlow = MutableSharedFlow<TrackingState>(replay = 1)

    // the public version of our flow is a SharedFlow as it's Read only, supports multiple consumers,
    // and will stay active regardless of collectors
    val state: Flow<TrackingState> = internalFlow.asSharedFlow()

    fun start() {
        if (job?.isActive == true) {
            stop()
        }
        locationProvider.start()
        job = appCoroutineScope.launch {
            when (val result = startActivityUsecase.invoke()) {
                is StartActivityUsecaseResult.Success -> {
                    internalFlow.emit(TrackingState.Started(result.activityId))
                    locationProvider.locationUpdated.collect { onNewLocation(result.activityId, it) }
                }
                is StartActivityUsecaseResult.Error -> {
                    Timber.e("Failed to start Activity")
                    stop()
                }
            }
        }
    }

    fun stop() {
        locationProvider.stop()
        appCoroutineScope.launch {
            internalFlow.emit(TrackingState.Stopped)
        }
        job?.cancel()
        job = null
    }

    // Note: I couldn't get the unit test correctly configured within timeline to test
    // locationProvider.locationUpdated.collect so opt'd to call the onNewLocation directly
    @TestOnly
    suspend fun onNewLocation(activityId: String, currentLocation: Location) {
        when (newLocationUsecase.invoke(activityId, previousLocation, currentLocation)) {
            is NewLocationUsecaseResult.Valid -> {
                // only update the previous location when the current Location is valid
                previousLocation = currentLocation
            }
            is NewLocationUsecaseResult.Invalid -> {
                // no op invalid locations are ignored
            }
        }
    }
}
