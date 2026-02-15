package jp.co.ndk_group.messay_duck_hunt.core.utils

import androidx.compose.runtime.Composable

/// Created by Aabhash Shakya on 15/02/2026

interface ToastManager {
    fun showShort(message: String)
    fun showLong(message: String)
}

@Composable
expect fun getToastManager(): ToastManager