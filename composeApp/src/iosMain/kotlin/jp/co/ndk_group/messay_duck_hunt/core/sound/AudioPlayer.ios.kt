package jp.co.ndk_group.messay_duck_hunt.core.sound

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer {

    private val players = mutableListOf<AVAudioPlayer>()

    actual fun playSound(uri: String) {
        val url = NSURL.URLWithString(uri) ?: return
        val data = NSData.dataWithContentsOfURL(url) ?: return

        val player = AVAudioPlayer(data = data, error = null)
        player?.let {
            players.add(it)
            it.play()

            // Clean up after playing (you might want to add a completion handler)
            // For now, just keep references to allow simultaneous playback
        }
    }

    actual fun release() {
        players.forEach { it.stop() }
        players.clear()
    }
}

@Composable
actual fun createAudioPlayer(): AudioPlayer {
    return remember { AudioPlayer() }
}