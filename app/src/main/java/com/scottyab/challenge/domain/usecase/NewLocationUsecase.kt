package com.scottyab.challenge.domain.usecase

import com.scottyab.challenge.domain.LocationCalculator
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.model.Location
import timber.log.Timber

class NewLocationUsecase(
    private val locationCalculator: LocationCalculator,
    private val snapshotRepository: SnapshotRepository
) {

    suspend operator fun invoke(
        activityId: String,
        previousLocation: Location?,
        currentLocation: Location
    ): NewLocationUsecaseResult {
        return if (isLocationValid(previousLocation, currentLocation)) {
            snapshotRepository.addSnapshot(activityId, currentLocation)
            NewLocationUsecaseResult.Valid(currentLocation)
        } else {
            NewLocationUsecaseResult.Invalid
        }
    }

    private fun isLocationValid(previousLocation: Location?, currentLocation: Location): Boolean {
        if (previousLocation == null) return true

        // Even though we have the LocationRequest setup with a min distance interval
        // we validate that the currentLocation more than 100m from previouslocation
        val distance = locationCalculator.distanceBetween(
            startLatitude = previousLocation.latitude.toDouble(),
            startLongitude = previousLocation.longitude.toDouble(),
            endLatitude = currentLocation.latitude.toDouble(),
            endLongitude = currentLocation.longitude.toDouble()
        )

        Timber.d("Distance between the points $distance")

        return distance > MIN_VALID_DISTANCE_METERS
    }

    companion object {
        const val MIN_VALID_DISTANCE_METERS = 100
    }
}

sealed class NewLocationUsecaseResult {

    data class Valid(val location: Location) : NewLocationUsecaseResult()
    object Invalid : NewLocationUsecaseResult()
}
