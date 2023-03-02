package com.scottyab.challenge.domain.mappers

import com.scottyab.challenge.data.datasource.local.db.SnapshotDb
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.IdGenerator
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.Photo
import com.scottyab.challenge.domain.model.Snapshot

class SnapshotMapper(private val idGenerator: IdGenerator) {

    fun toDomain(snapshotDbList: List<SnapshotDb>) = snapshotDbList.map { toDomain(it) }

    fun toDomain(db: SnapshotDb) = Snapshot(
        id = db.photoId,
        activityId = db.activityId,
        imageUrl = db.imageUrl,
        title = db.title,
        recordedAt = db.createdAt,
        location = Location(latitude = db.latitude, longitude = db.longitude)
    )

    fun toData(activityId: String, photo: Photo, location: Location) = SnapshotDb(
        id = idGenerator.generate(),
        activityId = activityId,
        photoId = photo.id,
        imageUrl = photo.imageUrl,
        title = photo.title,
        createdAt = nowUTC(),
        latitude = location.latitude,
        longitude = location.longitude
    )
}
