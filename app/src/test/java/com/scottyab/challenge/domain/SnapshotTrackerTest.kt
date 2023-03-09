@file:OptIn(ExperimentalCoroutinesApi::class)

package com.scottyab.challenge.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.datasource.location.LocationProvider
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.TrackingState
import com.scottyab.challenge.domain.usecase.NewLocationUsecase
import com.scottyab.challenge.domain.usecase.NewLocationUsecaseResult
import com.scottyab.challenge.domain.usecase.StartActivityUsecase
import com.scottyab.challenge.domain.usecase.StartActivityUsecaseResult
import com.scottyab.challenge.isInstanceOf
import com.scottyab.challenge.presentation.common.AppCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SnapshotTrackerTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var startActivityUsecase: StartActivityUsecase

    @Mock
    private lateinit var newLocationUsecase: NewLocationUsecase

    @Mock
    private lateinit var locationProvider: LocationProvider

    private lateinit var sut: SnapshotTracker

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        sut = SnapshotTracker(
            locationProvider = locationProvider,
            newLocationUsecase = newLocationUsecase,
            startActivityUsecase = startActivityUsecase,
            appCoroutineScope = AppCoroutineScope(testDispatcher)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancelChildren()
    }

    @Test
    fun `start SHOULD start the location provider AND update state to started`() {
        runTest {
            whenever(startActivityUsecase.invoke())
                .thenReturn(StartActivityUsecaseResult.Success(anActivityId))
            sut.start()

            sut.state.test {
                assertThat(awaitItem()).isInstanceOf<TrackingState.Started> {
                    assertThat(it.activityId).isEqualTo(anActivityId)
                }
                anActivityId
            }
            verify(locationProvider).start()
        }
    }

    @Test
    fun `stop SHOULD stop the location provider AND update state to stopped`() {
        runTest {
            sut.stop()

            sut.state.test {
                assertThat(awaitItem()).isEqualTo(TrackingState.Stopped)
            }
            verify(locationProvider).stop()
        }
    }

    @Test
    fun `onNewLocation SHOULD update previousLocation WHEN NewLocationUsecaseResult valid`() {
        runTest {
            whenever(
                newLocationUsecase.invoke(
                    activityId = anActivityId,
                    previousLocation = null,
                    currentLocation = aNewLocation
                )
            ).thenReturn(NewLocationUsecaseResult.Valid(aNewLocation))

            sut.onNewLocation(anActivityId, aNewLocation)

            assertThat(sut.previousLocation).isEqualTo(aNewLocation)
        }
    }

    @Test
    fun `onNewLocation SHOULD NOT update previousLocation WHEN NewLocationUsecaseResult invalid`() {
        runTest {
            whenever(
                newLocationUsecase.invoke(
                    activityId = anActivityId,
                    previousLocation = null,
                    currentLocation = aNewLocation
                )
            ).thenReturn(NewLocationUsecaseResult.Invalid)

            sut.onNewLocation(anActivityId, aNewLocation)

            assertThat(sut.previousLocation).isNull()
        }
    }

    companion object {
        private const val anActivityId = "123abc"
        private val aNewLocation = Location(latitude = "1.0", longitude = "1.0")
    }
}
