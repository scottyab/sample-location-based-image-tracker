package com.scottyab.challenge.data

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.datasource.local.db.SnapshotDao
import com.scottyab.challenge.data.datasource.local.db.SnapshotDb
import com.scottyab.challenge.data.datasource.remote.ApiPhoto
import com.scottyab.challenge.data.datasource.remote.ApiPhotosList
import com.scottyab.challenge.data.datasource.remote.ApiPhotosResponse
import com.scottyab.challenge.data.datasource.remote.FlickrService
import com.scottyab.challenge.domain.mappers.PhotoMapper
import com.scottyab.challenge.domain.mappers.SnapshotMapper
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.Photo
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner::class)
class RealSnapshotRepositoryTest {

    @Mock
    private lateinit var flickrService: FlickrService

    @Mock
    private lateinit var snapshotMapper: SnapshotMapper

    @Mock
    private lateinit var photoMapper: PhotoMapper

    @Mock
    private lateinit var snapshotDao: SnapshotDao

    private lateinit var sut: RealSnapshotRepository

    @Before
    fun setUp() {
        sut = RealSnapshotRepository(flickrService, snapshotMapper, photoMapper, snapshotDao)
    }

    @Test
    fun `addSnapshot SHOULD insert to DB WHEN DB is empty`() {
        runTest {
            whenever(flickrService.searchPhotos(lat, lon)).thenReturn(apiResponse)
            whenever(snapshotDao.getSnapshots(anActivityId)).thenReturn(emptyList())
            whenever(snapshotMapper.toData(anActivityId, aPhoto, aLocation)).thenReturn(aSnapShot)
            whenever(photoMapper.toDomain(aPhotoApi)).thenReturn(aPhoto)

            sut.addSnapshot(anActivityId, aLocation)

            verify(snapshotDao).insert(aSnapShot)
        }
    }

    @Test
    fun `addSnapshot SHOULD insert to DB WHEN DB does not contain any matching PhotoId`() {
        runTest {
            whenever(flickrService.searchPhotos(lat, lon)).thenReturn(apiResponseWithAnother)
            whenever(snapshotDao.getSnapshots(anActivityId)).thenReturn(listOf(aSnapShot))
            whenever(snapshotMapper.toData(anActivityId, anotherPhoto, aLocation)).thenReturn(
                anotherSnapshotDb
            )
            whenever(photoMapper.toDomain(aPhotoApi)).thenReturn(aPhoto)
            whenever(photoMapper.toDomain(anotherPhotoApi)).thenReturn(anotherPhoto)

            sut.addSnapshot(anActivityId, aLocation)

            verify(snapshotDao).insert(anotherSnapshotDb)
        }
    }

    @Test
    fun `addSnapshot SHOULD not insert WHEN API throws Error`() {
        runTest {
            whenever(flickrService.searchPhotos(lat, lon)).thenThrow(IOException())

            sut.addSnapshot(anActivityId, aLocation)

            verify(snapshotDao, never()).insert(any())
        }
    }

    @Test
    fun `addSnapshot SHOULD not insert a duplicate photo WHEN DB contains matching PhotoId`() {
        runTest {
            whenever(flickrService.searchPhotos(lat, lon)).thenReturn(apiResponse)
            whenever(photoMapper.toDomain(aPhotoApi)).thenReturn(aPhoto)
            whenever(snapshotDao.getSnapshots(anActivityId)).thenReturn(listOf(aSnapShot))

            sut.addSnapshot(anActivityId, aLocation)

            verify(snapshotDao, never()).insert(any())
        }
    }

    @Test
    fun `addSnapshot SHOULD not insert to DB WHEN API returns empty photo list`() {
        runTest {
            whenever(flickrService.searchPhotos(lat, lon)).thenReturn(emptyApiResponse)

            sut.addSnapshot(anActivityId, aLocation)

            verify(snapshotDao, never()).insert(any())
        }
    }

    @Test
    fun `reset SHOULD delete all the rows in the DB`() {
        runTest {
            sut.reset(anActivityId)

            verify(snapshotDao).deleteAllRows(anActivityId)
        }
    }

    private companion object {
        const val anActivityId = "123abc"
        const val lat = "lat"
        const val lon = "lon"
        val aLocation = Location(lat, lon)

        val emptyApiResponse = ApiPhotosResponse(
            photos = ApiPhotosList(
                page = 0,
                pages = 0,
                total = "",
                photo = emptyList()
            )
        )

        const val photoId = "photoId"
        const val anotherPhotoId = "anotherPhotoId"
        const val imageUrl = "my.jpg"

        val aPhoto = Photo(id = photoId, imageUrl = imageUrl, title = "")
        val aSnapShot = SnapshotDb(
            id = "any",
            photoId = photoId,
            imageUrl = imageUrl,
            activityId = anActivityId,
            title = "",
            createdAt = LocalDateTime.now(),
            latitude = "",
            longitude = ""
        )

        val aPhotoApi = ApiPhoto(id = photoId, imageUrl = imageUrl, title = "")
        val anotherPhoto = aPhoto.copy(id = anotherPhotoId)
        val anotherPhotoApi = aPhotoApi.copy(id = anotherPhotoId)
        val anotherSnapshotDb = aSnapShot.copy(photoId = anotherPhotoId)

        val apiResponse = ApiPhotosResponse(
            photos = ApiPhotosList(
                page = 0,
                pages = 0,
                total = "",
                photo = listOf(aPhotoApi, aPhotoApi, aPhotoApi)
            )
        )

        val apiResponseWithAnother = ApiPhotosResponse(
            photos = ApiPhotosList(
                page = 0,
                pages = 0,
                total = "",
                photo = listOf(aPhotoApi, anotherPhotoApi)
            )
        )
    }
}
