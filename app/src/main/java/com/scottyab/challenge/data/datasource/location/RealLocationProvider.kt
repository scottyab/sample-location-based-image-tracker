package com.scottyab.challenge.data.datasource.location

import android.content.Context
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.presentation.common.AppCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class RealLocationProvider(
    private val context: Context,
    private val appCoroutineScope: AppCoroutineScope
) : LocationProvider {

    private val internalFlow = MutableSharedFlow<Location>(replay = 0)
    override val locationUpdated: SharedFlow<Location> = internalFlow.asSharedFlow()

    internal fun locationUpdated(currentLocation: Location) {
        appCoroutineScope.launch {
            internalFlow.emit(currentLocation)
        }
    }

    override fun start() {
        Timber.d("Requesting Service start")

        LocationService.start(context)
    }

    override fun stop() {
        Timber.d("Requesting Service Stop")

        LocationService.stop(context)
    }
}
