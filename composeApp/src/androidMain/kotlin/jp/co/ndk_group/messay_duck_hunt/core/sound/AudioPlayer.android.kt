package jp.co.ndk_group.messay_duck_hunt.core.sound

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

actual class AudioPlayer(private val context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    init {
        // Set the listener once during initialization
        soundPool.setOnLoadCompleteListener { pool, soundId, status ->
            if (status == 0) {
                pool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
    }

    actual fun playSound(uri: String) {
        val assetPath = uri.substringAfter("file:///android_asset/")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val afd: AssetFileDescriptor = context.assets.openFd(assetPath)
                soundPool.load(afd, 1)
                afd.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    actual fun release() {
        soundPool.release()
    }
}

@Composable
actual fun createAudioPlayer(): AudioPlayer {
    val context = LocalContext.current
    return remember { AudioPlayer(context) }
}