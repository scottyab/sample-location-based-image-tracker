package com.scottyab.challenge.data.datasource.location

import android.content.Context
import com.scottyab.challenge.domain.model.Location
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * This mocks up the Location provider using lot/longs from assets json file.
 * Used to perform integration test of the Flickr API call/parsing, DB and UI code.
 */
class SampleLocationProvider(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val moshi: Moshi,
    private val updateIntervalInMilli: Long = 4_000L
) : LocationProvider {

    private var job: Job? = null
    private val internalFlow = MutableSharedFlow<Location>(replay = 0)

    override val locationUpdated: SharedFlow<Location> = internalFlow.asSharedFlow()

    override fun start() {
        if (job?.isActive == true) {
            stop()
        }
        val sampleLocations = loadSampleDataFromAssets()
        job = coroutineScope.launch {
            sampleLocations.forEach { location ->
                internalFlow.emit(location)
                delay(updateIntervalInMilli)
            }
        }
    }

    override fun stop() {
        job?.cancel()
        job = null
    }

    private fun loadSampleDataFromAssets(): List<Location> {
        val sampleJson: String =
            context.assets.open(FILENAME).bufferedReader().use { it.readText() }

        val adapter = moshi.adapter(SampleItems::class.java)
        val sampleItems = adapter.fromJson(sampleJson)

        return sampleItems?.items?.map { it.toLocation() } ?: error("Unable to load $FILENAME")
    }

    companion object {
        private const val FILENAME = "sample_locations.json"
    }
}

data class SampleItems(val items: List<SampleLatLong>)

data class SampleLatLong(val lat: String, val lon: String) {
    fun toLocation() = Location(latitude = lat, longitude = lon)
}
