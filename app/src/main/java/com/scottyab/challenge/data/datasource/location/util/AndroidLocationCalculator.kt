package com.scottyab.challenge.data.datasource.location.util

import android.location.Location
import com.scottyab.challenge.domain.LocationCalculator

/**
 * Utilises the android.location.Location distanceTo. Distance is defined using the WGS84 ellipsoid
 */
class AndroidLocationCalculator : LocationCalculator {

    override fun distanceBetween(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float {
        val start = Location("start").apply {
            latitude = startLatitude
            longitude = startLongitude
        }

        val end = Location("end").apply {
            latitude = endLatitude
            longitude = endLongitude
        }

        return start.distanceTo(end)
    }
}
