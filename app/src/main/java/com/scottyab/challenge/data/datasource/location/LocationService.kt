package com.scottyab.challenge.data.datasource.location

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.scottyab.challenge.domain.mappers.LocationMapper
import org.koin.android.ext.android.inject
import timber.log.Timber

class LocationService : Service() {

    private val realLocationProvider by inject<RealLocationProvider>()
    private val notificationHelper by inject<LocationServiceNotificationHelper>()

    private val locationMapper = LocationMapper()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            Timber.d("New :locationResult $locationResult")
            locationResult.lastLocation?.let {
                val mappedLocation = locationMapper.toDomain(it)
                realLocationProvider.locationUpdated(mappedLocation)
            }
        }
    }

    private val locationRequest = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMinUpdateDistanceMeters(MIN_UPDATE_DISTANCE)
        .build()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (val action: String? = intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> handleStart()
            ACTION_STOP_FOREGROUND_SERVICE -> handleStop()
            else -> Timber.e("Unsupported Intent action $action")
        }
        return START_NOT_STICKY
    }

    private fun handleStop() {
        Timber.d("Stopping the Location Service")
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun handleStart() {
        Timber.d("Starting the Location Service")
        val notification = notificationHelper.createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FOREGROUND_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        }
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        Timber.d("Starting location updates")
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    override fun onDestroy() {
        Timber.d("Stopping location updates")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val LOCATION_UPDATE_INTERVAL = 7200L // based on 5km per hour, roughly 1m12s per 100m.

        const val MIN_UPDATE_DISTANCE = 105f // set the threshold slightly above 100m to allow inaccuracy

        const val FOREGROUND_NOTIFICATION_ID = 100

        private const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        private const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        fun start(context: Context) = Intent(context, LocationService::class.java).apply {
            action = ACTION_START_FOREGROUND_SERVICE
        }.also { context.startForegroundService(it) }

        fun stop(context: Context) = Intent(context, LocationService::class.java).apply {
            action = ACTION_STOP_FOREGROUND_SERVICE
        }.also {
            context.stopService(it)
        }
    }
}
