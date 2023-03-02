package com.scottyab.challenge.domain.model

import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.datasource.local.db.SnapshotDb
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.IdGenerator
import com.scottyab.challenge.domain.mappers.SnapshotMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SnapshotMapperTest {

    @Mock
    private lateinit var idGenerator: IdGenerator

    private lateinit var sut: SnapshotMapper

    @Before
    fun setUp() {
        sut = SnapshotMapper(idGenerator)
    }

    @Test
    fun `toDomain SHOULD map DB correctly to Domain`() {
        val db = SnapshotDb(
            id = "123",
            photoId = "photoId",
            activityId = "activityId",
            imageUrl = "aUrl",
            title = "title",
            createdAt = nowUTC(),
            latitude = "lat",
            longitude = "long"
        )
        val result = sut.toDomain(db)

        assertThat(result).satisfies {
            // id is mapped to photoId
            assertThat(it.id).isEqualTo(db.photoId)
            assertThat(it.imageUrl).isEqualTo(db.imageUrl)
            assertThat(it.title).isEqualTo(db.title)
            assertThat(it.activityId).isEqualTo(db.activityId)
            // useful to confirm they haven't been flipped by accident
            assertThat(it.location.latitude).isEqualTo(db.latitude)
            assertThat(it.location.longitude).isEqualTo(db.longitude)
        }
    }

    @Test
    fun `toData SHOULD map Domain correctly to Db`() {
        val snapShotId = "foo456"
        val activityId = "123abc"
        val location = Location("lat", "lon")
        val photo = Photo(id = "photoId", imageUrl = "aUrl", title = "title")
        whenever(idGenerator.generate()).thenReturn(snapShotId)

        val result = sut.toData(activityId, photo, location)

        assertThat(result).satisfies {
            assertThat(it.id).isEqualTo(snapShotId)
            assertThat(it.activityId).isEqualTo(activityId)
            assertThat(it.photoId).isEqualTo(photo.id)
            assertThat(it.imageUrl).isEqualTo(photo.imageUrl)
            assertThat(it.title).isEqualTo(photo.title)
            // useful to confirm they haven't been flipped by accident
            assertThat(it.latitude).isEqualTo(location.latitude)
            assertThat(it.longitude).isEqualTo(location.longitude)
        }
    }
}
