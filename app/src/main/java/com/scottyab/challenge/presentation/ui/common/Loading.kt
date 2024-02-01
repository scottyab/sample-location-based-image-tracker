package com.scottyab.challenge.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun AppProgressIndicator(
    modifier: Modifier = Modifier,
    progressContentDescription: String = "Loading",
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier.fillMaxSize().semantics {
                contentDescription = progressContentDescription
            },
    ) {
        CircularProgressIndicator(
            modifier = modifier.padding(8.dp).size(36.dp),
        )
    }
}
