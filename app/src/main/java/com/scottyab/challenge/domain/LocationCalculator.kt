package com.scottyab.challenge.domain

interface LocationCalculator {

    /**
     * @return Returns the approximate distance in meters between start and end */
    fun distanceBetween(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): Float
}
