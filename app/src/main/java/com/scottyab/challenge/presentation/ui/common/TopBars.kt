package com.scottyab.challenge.presentation.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.scottyab.challenge.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(
    titleText: String,
    onBackIconClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(titleText) },
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            IconButton(onClick = { onBackIconClick.invoke() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.back_button))
            }
        },
    )
}
