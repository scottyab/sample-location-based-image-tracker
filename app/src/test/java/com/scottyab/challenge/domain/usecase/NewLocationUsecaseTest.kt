package com.scottyab.challenge.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.domain.LocationCalculator
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.model.Location
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NewLocationUsecaseTest {
    @Mock
    private lateinit var locationCalculator: LocationCalculator
    @Mock
    private lateinit var snapshotRepository: SnapshotRepository

    private lateinit var sut: NewLocationUsecase

    @Before
    fun setUp() {
        sut = NewLocationUsecase(locationCalculator, snapshotRepository)
    }

    @Test
    fun `onNewLocation SHOULD add a snapshot WHEN previousLocation is null`() {
        runTest {

            sut.invoke(anActivityId, null, aLocation)

            verifyZeroInteractions(locationCalculator)
            verify(snapshotRepository).addSnapshot(anActivityId, aLocation)
        }
    }

    @Test
    fun `onNewLocation SHOULD add snapshot WHEN distanceBetween above 100`() {
        runTest {
            whenever(locationCalculator.distanceBetween(any(), any(), any(), any()))
                .thenReturn(120f)

            sut.invoke(anActivityId, aLocation, aNewLocation)

            verify(snapshotRepository).addSnapshot(anActivityId, aNewLocation)
        }
    }

    @Test
    fun `onNewLocation SHOULD not add snapshot WHEN distanceBetween below 100`() {
        runTest {
            whenever(locationCalculator.distanceBetween(any(), any(), any(), any()))
                .thenReturn(20f)

            sut.invoke(anActivityId, aLocation, aNewLocation)

            verifyZeroInteractions(snapshotRepository)
        }
    }

    companion object {
        private const val anActivityId = "123abc"
        private val aLocation = Location(latitude = "1.0", longitude = "1.0")
        private val aNewLocation = Location(latitude = "1.1", longitude = "1.1")
    }
} 
