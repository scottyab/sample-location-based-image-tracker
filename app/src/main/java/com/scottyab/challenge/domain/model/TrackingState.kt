package com.scottyab.challenge.domain.model

sealed class TrackingState {

    data class Started(val activityId: String) : TrackingState()
    object Stopped : TrackingState()
}
