package com.scottyab.challenge.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.usecase.StopActivityUsecaseResult.ErrorNoActivityStarted
import com.scottyab.challenge.domain.usecase.StopActivityUsecaseResult.Success
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StopActivityUsecaseTest {

    @Mock
    private lateinit var activityRepository: ActivityRepository

    private lateinit var sut: StopActivityUsecase

    @Before
    fun setUp() {
        sut = StopActivityUsecase(activityRepository)
    }

    @Test
    fun `invoke SHOULD return success AND stop the current activity WHEN there is a current Activity`() {
        runTest {
            val activityId = "123"
            whenever(activityRepository.getCurrentActivityId()).thenReturn(activityId)

            val result = sut.invoke()

            verify(activityRepository).finishActivity(eq(activityId), any())
            assertThat(result).isInstanceOf(Success::class.java)
        }
    }

    @Test
    fun `invoke SHOULD return error WHEN no current activity`() {
        runTest {
            val result = sut.invoke()

            verify(activityRepository, never()).finishActivity(any(), any())
            assertThat(result).isInstanceOf(ErrorNoActivityStarted::class.java)
        }
    }
}
