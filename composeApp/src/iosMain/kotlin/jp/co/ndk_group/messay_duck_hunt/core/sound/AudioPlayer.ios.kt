package jp.co.ndk_group.messay_duck_hunt.core.sound

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class AudioPlayer {

    private val players = mutableListOf<AVAudioPlayer>()

    private val delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            players.remove(player)
        }
    }

    actual fun playSound(uri: String) {
        val url = NSURL.URLWithString(uri) ?: return
        val data = NSData.dataWithContentsOfURL(url) ?: return

        val player = AVAudioPlayer(data = data, error = null)
        player.let {
            it.delegate = delegate
            players.add(it)
            it.play()
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