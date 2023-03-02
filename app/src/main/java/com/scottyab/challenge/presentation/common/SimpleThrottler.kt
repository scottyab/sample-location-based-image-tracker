package com.scottyab.challenge.presentation.common

/**
 * Util to prevent accidental double taps
 */
class SimpleThrottler(private val minIntervalInMillis: Long = 1000) {

    private var lastEventTime = 0L

    fun throttle(code: () -> Unit) {
        val eventTime = System.currentTimeMillis()
        if (lastEventTime == 0L || eventTime - lastEventTime > minIntervalInMillis) {
            lastEventTime = eventTime
            code()
        }
    }
}
