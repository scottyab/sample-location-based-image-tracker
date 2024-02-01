package com.scottyab.challenge.presentation.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val SampleAppColorScheme =
    lightColorScheme(
        primary = AppBlue,
        secondary = Secondary,
        tertiary = VioletText,
        background = Grey200,
        surface = Secondary,
        surfaceVariant = Grey200,
        onPrimary = White,
        onSecondary = White,
        onTertiary = Black,
        onBackground = Black,
        onSurface = White,
    )

@Composable
fun SampleAppTheme(
    colorScheme: ColorScheme = SampleAppColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography =
            Typography(
                labelLarge =
                    TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                    ),
                titleLarge =
                    TextStyle(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.W600,
                        lineHeight = 24.sp,
                    ),
            ),
        content = content,
    )
}
