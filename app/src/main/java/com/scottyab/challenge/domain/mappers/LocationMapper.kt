package com.scottyab.challenge.domain.mappers

import com.scottyab.challenge.domain.model.Location

/**
 * TODO this mapper is in the wrong package, we should *not* be referencing Android here in domain
 */
class LocationMapper {

    fun toDomain(location: android.location.Location) = Location(
        latitude = location.latitude.toString(),
        longitude = location.longitude.toString()
    )
}
