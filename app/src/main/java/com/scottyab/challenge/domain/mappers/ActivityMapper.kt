package com.scottyab.challenge.domain.mappers

import com.scottyab.challenge.data.datasource.local.db.ActivityDb
import com.scottyab.challenge.domain.model.Activity

class ActivityMapper {

    fun toDomain(dbItems: List<ActivityDb>) = dbItems.map { toDomain(it) }

    fun toDomain(db: ActivityDb) = Activity(
        id = db.id,
        title = db.title,
        startedAt = db.createdAt,
        finishedAt = db.finishedAt
    )
}
