package jp.co.ndk_group.messay_duck_hunt.core.sound

import androidx.compose.runtime.Composable

/// Created by Aabhash Shakya on 14/02/2026

@Composable
expect fun createAudioPlayer(): AudioPlayer

expect class AudioPlayer {
    fun playSound(uri: String)
    fun release()
}