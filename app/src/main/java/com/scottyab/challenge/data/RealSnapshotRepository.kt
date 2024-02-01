package com.scottyab.challenge.data

import com.scottyab.challenge.data.datasource.local.db.SnapshotDao
import com.scottyab.challenge.data.datasource.remote.FlickrService
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.mappers.PhotoMapper
import com.scottyab.challenge.domain.mappers.SnapshotMapper
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.Photo
import com.scottyab.challenge.domain.model.Snapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException

class RealSnapshotRepository(
    private val flickrService: FlickrService,
    private val snapshotMapper: SnapshotMapper,
    private val photoMapper: PhotoMapper,
    private val snapshotDao: SnapshotDao
) : SnapshotRepository {
    override fun getSnapshots(activityId: String): Flow<List<Snapshot>> =
        snapshotDao.observeSnapshots(activityId).map(snapshotMapper::toDomain)

    override suspend fun getSnapShot(snapshotId: String): Snapshot = snapshotMapper.toDomain(snapshotDao.getSnapshot(snapshotId))

    override suspend fun addSnapshot(
        activityId: String,
        location: Location
    ) {
        try {
            val photos = downloadPhotos(location)
            // if there is not a unique photo skip adding it
            val photo = findUniquePhoto(activityId, photos) ?: return

            val snapshotDb = snapshotMapper.toData(activityId, photo, location)
            Timber.d("Inserting snapshot ${snapshotDb.title} into DB at ${snapshotDb.createdAt}")
            snapshotDao.insert(snapshotDb)
        } catch (e: IOException) {
            Timber.e(e, "Error adding snapshot")
        }
    }

    /**
     * @return the first Photo from the list that is not already have saved in the DB
     */
    private fun findUniquePhoto(
        activityId: String,
        photos: List<Photo>
    ): Photo? {
        if (photos.isEmpty()) return null

        val exitingPhotoIds = snapshotDao.getSnapshots(activityId).map { it.photoId }

        photos.forEach { downloadedPhoto ->
            // use the first downloadedPhoto that we don't already have existing Id for
            if (exitingPhotoIds.none { it == downloadedPhoto.id }) {
                return downloadedPhoto
            }
        }
        return null
    }

    private suspend fun downloadPhotos(location: Location): List<Photo> {
        val response =
            flickrService.searchPhotos(latitude = location.latitude, longitude = location.longitude)

        return response.photos.photo.map(photoMapper::toDomain)
    }

    override suspend fun reset(activityId: String) {
        snapshotDao.deleteAllRows(activityId)
    }
}
