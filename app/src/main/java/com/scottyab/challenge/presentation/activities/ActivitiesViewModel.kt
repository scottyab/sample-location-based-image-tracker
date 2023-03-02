package com.scottyab.challenge.presentation.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.scottyab.challenge.domain.ActivityRepository
import com.scottyab.challenge.domain.model.Activity
import com.scottyab.challenge.presentation.common.BaseViewModel
import com.scottyab.challenge.presentation.common.SingleLiveEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ActivitiesViewModel(
    private val activityRepository: ActivityRepository,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ActivitiesState>(ActivitiesState()) {

    private val openNewActivity = SingleLiveEvent<Unit>()
    val openNewActivityLiveData: LiveData<Unit> get() = openNewActivity

    private val openExistingActivity = SingleLiveEvent<String>()
    val openExistingActivityLiveData: LiveData<String> get() = openExistingActivity

    init {
        setState { ActivitiesState() }

        viewModelScope.launch {
            activityRepository.getActivities()
                .flowOn(backgroundDispatcher)
                .map { activities -> setState { copy(activities = activities) } }.collect()
        }
    }

    fun startNewActivityClick() {
        openNewActivity.call()
    }

    fun activityItemClick(activityId: String) {
        openExistingActivity.value = activityId
    }
}

data class ActivitiesState(
    val activities: List<Activity> = emptyList()
) {
    val shouldShowEmpty = activities.isEmpty()
}
