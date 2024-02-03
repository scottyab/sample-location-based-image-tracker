package com.scottyab.challenge.presentation.details

import androidx.lifecycle.viewModelScope
import com.scottyab.challenge.domain.SnapshotRepository
import com.scottyab.challenge.presentation.common.BaseViewModel
import com.scottyab.challenge.presentation.details.DetailsUiEvent.ShareClick
import com.scottyab.challenge.presentation.details.DetailsUiEvent.SnackMessageShown
import com.scottyab.challenge.presentation.details.DetailsUiEvent.UpClick
import com.scottyab.challenge.presentation.snapshots.SnapshotUiMapper
import com.scottyab.challenge.presentation.snapshots.model.EMPTY_SNAPSHOT
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(
    private val snapshotRepository: SnapshotRepository,
    private val snapshotUiMapper: SnapshotUiMapper,
    private val snapshotId: String,
    private val backgroundDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DetailsState>(DetailsState()) {
    init {
        viewModelScope.launch {
            val snapshot =
                withContext(backgroundDispatcher) {
                    snapshotUiMapper.toUi(snapshotRepository.getSnapShot(snapshotId))
                }
            setState { copy(aSnapshot = snapshot) }
        }
    }

    fun handleUiEvent(detailsUiEvent: DetailsUiEvent) {
        when (detailsUiEvent) {
            is ShareClick -> handleShare(detailsUiEvent.url)
            is SnackMessageShown -> setState { copy(snackMessage = "") }
            is UpClick -> setState { copy(gotoSnapshots = true) }
        }
    }

    private fun handleShare(url: String) {
        setState { copy(snackMessage = "Sharing {$url}") }
    }
}

data class DetailsState(
    val aSnapshot: SnapshotUi = EMPTY_SNAPSHOT,
    val showLoading: Boolean = false,
    val snackMessage: String = "",
    val gotoSnapshots: Boolean = false
) {
    val isEmpty = aSnapshot == EMPTY_SNAPSHOT
    val title = if (isEmpty) "" else "Details"
    val showSnackMessage = snackMessage.isNotEmpty()
}

sealed interface DetailsUiEvent {
    data object UpClick : DetailsUiEvent

    data object SnackMessageShown : DetailsUiEvent

    data class ShareClick(val url: String) : DetailsUiEvent
}
