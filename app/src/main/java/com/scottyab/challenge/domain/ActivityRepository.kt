package com.scottyab.challenge.domain

import com.scottyab.challenge.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ActivityRepository {

    fun getCurrentActivityId(): String?

    fun startActivity(id: String)

    fun getActivities(): Flow<List<Activity>>

    suspend fun newActivity(title: String): Activity

    suspend fun updateActivity(activityId: String, title: String, finishedAt: LocalDateTime)

    suspend fun delete(activityId: String)
}
