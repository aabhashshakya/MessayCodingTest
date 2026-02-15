package jp.co.ndk_group.messay_duck_hunt.core.utils

/// Created by Aabhash Shakya on 15/02/2026

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(enabled: Boolean = true, onBack: () -> Unit)