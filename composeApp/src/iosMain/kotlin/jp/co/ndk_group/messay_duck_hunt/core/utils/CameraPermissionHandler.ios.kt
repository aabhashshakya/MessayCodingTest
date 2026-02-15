package jp.co.ndk_group.messay_duck_hunt.core.utils

import androidx.compose.runtime.Composable

import androidx.compose.runtime.*
import platform.AVFoundation.AVAuthorizationStatus
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

/**
 * iOS implementation of CameraPermissionHandler
 */
class IOSCameraPermissionHandler : CameraPermissionHandler {

    override fun isPermissionGranted(): Boolean {
        val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
        return status == AVAuthorizationStatusAuthorized
    }

    override fun requestPermission(onResult: (PermissionState) -> Unit) {
        AVCaptureDevice.requestAccessForMediaType(
            mediaType = AVMediaTypeVideo,
            completionHandler = { granted ->
                val newState = if (granted) {
                    PermissionState.GRANTED
                } else {
                    PermissionState.DENIED
                }
                onResult(newState)
            }
        )
    }

    override fun getPermissionState(): PermissionState {
        return when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> PermissionState.GRANTED
            AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> PermissionState.DENIED
            AVAuthorizationStatusNotDetermined -> PermissionState.NOT_DETERMINED
            else -> PermissionState.NOT_DETERMINED
        }
    }

    override fun openAppSettings() {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        if (settingsUrl != null && UIApplication.sharedApplication.canOpenURL(settingsUrl)) {
            UIApplication.sharedApplication.openURL(settingsUrl)
        }
    }
}

/**
 * iOS composable for camera permission handler
 */
@Composable
actual fun rememberCameraPermissionHandler(): CameraPermissionHandler {
    return remember { IOSCameraPermissionHandler() }
}