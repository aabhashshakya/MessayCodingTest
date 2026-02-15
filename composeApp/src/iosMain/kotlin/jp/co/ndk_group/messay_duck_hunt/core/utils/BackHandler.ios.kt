package jp.co.ndk_group.messay_duck_hunt.core.utils

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {
    //No back button on iOS
}