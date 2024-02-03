package com.scottyab.challenge.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.data.extensions.nowUTC
import com.scottyab.challenge.domain.ActivityNameGenerator
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.model.Activity
import com.scottyab.challenge.domain.usecase.StartActivityUsecaseResult.Success
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StartActivityUsecaseTest {

    @Mock
    private lateinit var activityRepository: ActivityRepository

    @Mock
    private lateinit var activityNameGenerator: ActivityNameGenerator

    private lateinit var sut: StartActivityUsecase

    @Before
    fun setUp() {
        sut = StartActivityUsecase(activityRepository, activityNameGenerator)
    }

    @Test
    fun `invoke SHOULD create new activity WHEN there is not current Activity`() {
        runTest {
            val activityName = "name"
            whenever(activityNameGenerator.generate()).thenReturn(activityName)
            val newActivityId = "123"
            val activity = Activity(id = newActivityId, title = "", startedAt = nowUTC(), finishedAt = null)
            whenever(activityRepository.newActivity(activityName)).thenReturn(activity)

            val result = sut.invoke()

            verify(activityRepository).newActivity(activityName)
            assertThat(result).isInstanceOf<Success> {
                assertThat(it.activityId).isEqualTo(newActivityId)
            }
        }
    }

    @Test
    fun `invoke SHOULD return current activity WHEN there is an existing current Activity`() {
        runTest {
            val existingActivityId = "123"
            whenever(activityRepository.getCurrentActivityId()).thenReturn(existingActivityId)

            val result = sut.invoke()

            verify(activityRepository, never()).newActivity(any())
            assertThat(result).isInstanceOf(Success::class.java) {
                assertThat(it.activityId).isEqualTo(existingActivityId)
            }
        }
    }
}
