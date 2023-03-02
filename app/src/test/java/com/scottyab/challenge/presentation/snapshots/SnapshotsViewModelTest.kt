package com.scottyab.challenge.presentation.snapshots

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.domain.SnapshotTracker
import com.scottyab.challenge.domain.model.Location
import com.scottyab.challenge.domain.model.Snapshot
import com.scottyab.challenge.domain.model.TrackingState.Started
import com.scottyab.challenge.domain.model.TrackingState.Stopped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.flowOf
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
class SnapshotsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: SnapshotRepository

    @Mock
    private lateinit var snapshotUiMapper: SnapshotUiMapper

    @Mock
    private lateinit var snapshotTracker: SnapshotTracker

    private lateinit var sut: SnapshotsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        sut = SnapshotsViewModel(
            snapshotRepository = repository,
            snapshotUiMapper = snapshotUiMapper,
            snapshotTracker = snapshotTracker,
            backgroundDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancelChildren()
    }

    @Test
    fun `observeSnapshots SHOULD update view state snapshots WHEN snapshots change`() {
        runTest {
            val snapshots = listOf(aSnapshot, aSnapshot)
            whenever(repository.getSnapshots(anActivityId)).thenReturn(flowOf(snapshots))
            sut = SnapshotsViewModel(repository, SnapshotUiMapper(), mock(), testDispatcher)

            sut.observeSnapshots(anActivityId)

            assertThat(sut.state.value!!).satisfies {
                assertThat(it.snapshots).hasSameSizeAs(snapshots)
            }
        }
    }

    @Test
    fun `init SHOULD update view state tracking state WHEN tracker state changes`() {
        runTest {
            val expectedState = Started(anActivityId)
            whenever(snapshotTracker.state).thenReturn(flowOf(expectedState))

            sut = SnapshotsViewModel(repository, mock(), snapshotTracker, testDispatcher)

            assertThat(sut.state.value!!).satisfies {
                assertThat(it.trackingState).isEqualTo(expectedState)
            }
        }
    }

    @Test
    fun `trackingClick SHOULD stop tracking WHEN current state is started`() {
        runTest {
            val state = SnapshotsState(trackingState = Started(anActivityId))
            sut.setState { state }

            sut.trackingClick()

            verify(snapshotTracker).stop()
        }
    }

    @Test
    fun `trackingClick SHOULD start tracking WHEN current state is stopped`() {
        runTest {
            val state = SnapshotsState(trackingState = Stopped)
            sut.setState { state }

            sut.trackingClick()

            verify(snapshotTracker).start()
        }
    }

    @Test
    fun `resetClick SHOULD call reset AND class snapshots from state`() {
        runTest {
            sut.setState {
                SnapshotsState(
                    activityId = anActivityId,
                    snapshots = listOf(mock(), mock())
                )
            }

            sut.resetClick()

            verify(repository).reset(anActivityId)
        }
    }

    @Test
    fun `resetClick SHOULD not call reset WHEN null`() {
        runTest {
            sut.setState { SnapshotsState(snapshots = listOf(mock(), mock())) }

            sut.resetClick()

            verifyZeroInteractions(repository)
        }
    }

    companion object {
        val anActivityId = "abc123"

        val aSnapshot = Snapshot(
            id = "id",
            imageUrl = "url",
            activityId = anActivityId,
            title = "title",
            recordedAt = nowUTC(),
            location = Location(latitude = "1.0", longitude = "1.0")
        )
    }
}
