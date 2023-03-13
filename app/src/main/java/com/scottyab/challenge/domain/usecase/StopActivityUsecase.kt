package com.scottyab.challenge.domain.usecase

import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.usecase.StopActivityUsecaseResult.ErrorNoActivityStarted
import com.scottyab.challenge.domain.usecase.StopActivityUsecaseResult.Success

class StopActivityUsecase(
    private val activityRepository: ActivityRepository
) {

    suspend operator fun invoke(): StopActivityUsecaseResult {
        val activityId = activityRepository.getCurrentActivityId() ?: return ErrorNoActivityStarted
        activityRepository.finishActivity(activityId, nowUTC())
        return Success(activityId)
    }
}

sealed class StopActivityUsecaseResult {
    data class Success(val activityId: String) : StopActivityUsecaseResult()
    object ErrorNoActivityStarted : StopActivityUsecaseResult()
}
