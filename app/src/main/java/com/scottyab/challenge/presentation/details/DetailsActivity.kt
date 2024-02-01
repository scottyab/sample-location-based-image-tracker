package com.scottyab.challenge.presentation.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import com.scottyab.challenge.presentation.ui.SampleAppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailsActivity : ComponentActivity() {
    private val viewModel: DetailsViewModel by viewModel {
        parametersOf(intent.getStringExtra(EXTRA_SNAPSHOT_ID))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState = viewModel.state.observeAsState(DetailsState()).value

            if (uiState.gotoSnapshots) {
                finish()
            }

            SampleAppTheme {
                DetailsScreen(uiState, viewModel::handleUiEvent)
            }
        }
    }

    companion object {
        private const val EXTRA_SNAPSHOT_ID = "EXTRA_SNAPSHOT_ID"

        private fun openIntent(context: Context) = Intent(context, DetailsActivity::class.java)

        fun openIntentExisting(
            context: Context,
            snapshotId: String,
        ) = openIntent(context).putExtra(EXTRA_SNAPSHOT_ID, snapshotId)
    }
}
