package com.scottyab.challenge.domain.usecase

import com.scottyab.challenge.domain.ActivityNameGenerator
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.usecase.StartActivityUsecaseResult.Success

class StartActivityUsecase(
    private val activityRepository: ActivityRepository,
    private val activityNameGenerator: ActivityNameGenerator
) {

    suspend operator fun invoke(): StartActivityUsecaseResult {
        val activityId = activityRepository.getCurrentActivityId() ?: createActivity()
        activityRepository.startActivity(activityId)
        return Success(activityId)
    }

    private suspend fun createActivity() =
        activityRepository.newActivity(activityNameGenerator.generate()).id
}

sealed class StartActivityUsecaseResult {
    data class Success(val activityId: String) : StartActivityUsecaseResult()
    object Error : StartActivityUsecaseResult()
}
