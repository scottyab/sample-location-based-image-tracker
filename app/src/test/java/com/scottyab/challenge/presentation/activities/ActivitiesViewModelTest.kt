package com.scottyab.challenge.presentation.activities

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.model.Activity
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
class ActivitiesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: ActivityRepository

    private lateinit var sut: ActivitiesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        sut = ActivitiesViewModel(
            activityRepository = repository,
            backgroundDispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cancelChildren()
    }

    @Test
    fun `init SHOULD observe ui state for activities`() {
        runTest {
            val expectedActivities = listOf(anActivity, anActivity.copy(id = "anotherId"))
            whenever(repository.getActivities()).thenReturn(flowOf(expectedActivities))

            sut = ActivitiesViewModel(
                activityRepository = repository,
                backgroundDispatcher = testDispatcher
            )

            assertThat(sut.state.value!!).satisfies {
                assertThat(it.activities).isEqualTo(expectedActivities)
            }
        }
    }

    private companion object {
        private val anActivity = Activity(id = "123", title = "title", startedAt = nowUTC(), finishedAt = null)
    }
}
