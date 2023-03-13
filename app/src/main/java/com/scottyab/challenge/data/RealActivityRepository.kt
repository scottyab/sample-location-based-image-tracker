package com.scottyab.challenge.data

import com.scottyab.challenge.data.datasource.local.db.ActivityDao
import com.scottyab.challenge.data.datasource.local.db.ActivityDb
import com.scottyab.challenge.data.datasource.local.db.CurrentActivityDb
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.IdGenerator
import com.scottyab.challenge.domain.mappers.ActivityMapper
import com.scottyab.challenge.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime

class RealActivityRepository(
    private val activityMapper: ActivityMapper,
    private val activityDao: ActivityDao,
    private val idGenerator: IdGenerator
) : ActivityRepository {

    override fun getActivities(): Flow<List<Activity>> =
        activityDao.observeActivities().map(activityMapper::toDomain)

    override suspend fun newActivity(title: String): Activity {
        val newActivity = ActivityDb(
            id = idGenerator.generate(),
            title = title,
            createdAt = nowUTC(),
            finishedAt = null
        )

        Timber.d("Inserting newActivity ${newActivity.title} into DB at ${newActivity.createdAt}")
        activityDao.insert(newActivity)
        return activityMapper.toDomain(newActivity)
    }

    override fun startActivity(id: String) {
        activityDao.clearCurrentActivity()
        activityDao.startActivity(CurrentActivityDb(activityId = id))
    }

    override fun getCurrentActivityId() = activityDao.getCurrentActivity().firstOrNull()?.activityId

    override suspend fun finishActivity(activityId: String, finishedAt: LocalDateTime) {
        activityDao.finish(activityId, finishedAt)
        activityDao.clearCurrentActivity()
    }

    override suspend fun delete(activityId: String) {
        activityDao.delete(activityId)
    }
}
