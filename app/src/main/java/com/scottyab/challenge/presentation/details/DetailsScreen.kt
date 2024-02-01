package com.scottyab.challenge.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.scottyab.challenge.R
import com.scottyab.challenge.presentation.details.DetailsUiEvent.ShareClick
import com.scottyab.challenge.presentation.details.DetailsUiEvent.SnackMessageShown
import com.scottyab.challenge.presentation.details.DetailsUiEvent.UpClick
import com.scottyab.challenge.presentation.snapshots.model.SnapshotUi
import com.scottyab.challenge.presentation.ui.SampleAppTheme
import com.scottyab.challenge.presentation.ui.common.AppProgressIndicator
import com.scottyab.challenge.presentation.ui.common.DefaultTopBar
import java.time.LocalDateTime

@Suppress("ktlint:standard:function-naming")
@Composable
fun DetailsScreen(
    uiState: DetailsState,
    uiEventReceiver: (DetailsUiEvent) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DefaultTopBar(
                uiState.title,
                onBackIconClick = { uiEventReceiver.invoke(UpClick) },
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        if (uiState.showSnackMessage) {
            LaunchedEffect(scope, uiState.snackMessage, snackbarHostState) {
                val result =
                    snackbarHostState.showSnackbar(
                        message = uiState.snackMessage,
                        duration = SnackbarDuration.Short,
                    )
                when (result) {
                    SnackbarResult.Dismissed -> uiEventReceiver.invoke(SnackMessageShown)
                    SnackbarResult.ActionPerformed -> { // No-Op
                    }
                }
            }
        }

        if (uiState.showLoading) {
            AppProgressIndicator()
        } else {
            Column(
                modifier =
                Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Column(
                    modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f),
                ) {
                    // content goes here
                    SnapShotDetailsComposable(
                        snapshot = uiState.aSnapshot,
                        shareClick = { uiEventReceiver.invoke(ShareClick(uiState.aSnapshot.imageUrl)) },
                    )
                }
            }
        }
    }
}

@Composable
fun SnapShotDetailsComposable(
    snapshot: SnapshotUi,
    shareClick: () -> Unit,
) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(4.dp),
    ) {
        SubcomposeAsyncImage(
            model = snapshot.imageUrl,
            contentDescription = stringResource(R.string.snapshot_image_cd),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth(),
        )

        BasicText(text = snapshot.title, style = MaterialTheme.typography.headlineLarge)
        BasicText(text = snapshot.recordedAtFormatted)
        Button(
            onClick = { shareClick.invoke() },
            modifier =
            Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(R.string.details_share_button_cd),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(R.string.details_share_button))
        }
    }
}

@Preview
@Composable
fun DetailsScreenPreview() {
    val snapshotUi =
        SnapshotUi(
            id = "123",
            title = "This is a title",
            imageUrl = "",
            recordedAt = LocalDateTime.now(),
        )
    val uiState =
        DetailsState(
            aSnapshot = snapshotUi,
        )
    SampleAppTheme {
        DetailsScreen(uiState) {}
    }
}
