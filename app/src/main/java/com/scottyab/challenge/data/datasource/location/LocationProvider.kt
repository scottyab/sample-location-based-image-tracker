package com.scottyab.challenge.data.datasource.location

import com.scottyab.challenge.domain.model.Location
import kotlinx.coroutines.flow.SharedFlow

interface LocationProvider {
    val locationUpdated: SharedFlow<Location>

    fun start()
    fun stop()
}
