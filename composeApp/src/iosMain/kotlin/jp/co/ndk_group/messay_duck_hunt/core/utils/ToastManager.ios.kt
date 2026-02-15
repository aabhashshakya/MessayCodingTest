package jp.co.ndk_group.messay_duck_hunt.core.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import platform.Foundation.NSTimer
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
class IOSToastManager : ToastManager {

    private fun showToast(message: String, duration: Double) {
        dispatch_async(dispatch_get_main_queue()) {
            val alertController = UIAlertController.alertControllerWithTitle(
                title = null,
                message = message,
                preferredStyle = UIAlertControllerStyleAlert
            )

            // Get the root view controller
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(
                alertController,
                animated = true,
                completion = null
            )

            // Auto-dismiss after duration
            NSTimer.scheduledTimerWithTimeInterval(
                duration,
                repeats = false
            ) {
                alertController.dismissViewControllerAnimated(true, completion = null)
            }
        }
    }

    override fun showShort(message: String) {
        showToast(message, 2.0)  // 2 seconds
    }

    override fun showLong(message: String) {
        showToast(message, 3.5)  // 3.5 seconds
    }
}

@Composable
actual fun getToastManager(): ToastManager {
    return remember { IOSToastManager() }
}