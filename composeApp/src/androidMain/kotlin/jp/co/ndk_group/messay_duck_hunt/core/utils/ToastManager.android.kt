package jp.co.ndk_group.messay_duck_hunt.core.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidToastManager(private val context: Context) : ToastManager {
    override fun showShort(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLong(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

@Composable
actual fun getToastManager(): ToastManager {
    val context = LocalContext.current
    return remember { AndroidToastManager(context) }
}