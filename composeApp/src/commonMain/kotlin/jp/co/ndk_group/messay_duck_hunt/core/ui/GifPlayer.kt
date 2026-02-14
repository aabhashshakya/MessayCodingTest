package jp.co.ndk_group.messay_duck_hunt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

/// Created by Aabhash Shakya on 14/02/2026

@Composable
expect fun GifPlayer(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null
)
