package jp.co.ndk_group.messay_duck_hunt.core.utils

import androidx.compose.runtime.Composable

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Android implementation of CameraPermissionHandler
 */
class AndroidCameraPermissionHandler(
    private val context: Context
) : CameraPermissionHandler {

    override fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission(onResult: (PermissionState) -> Unit) {
        // This method should not be called directly on Android
        // Use the launcher from the composable instead
    }

    override fun getPermissionState(): PermissionState {
        return if (isPermissionGranted()) {
            PermissionState.GRANTED
        } else {
            // Note: We can't easily detect PERMANENTLY_DENIED without requesting
            // This will be handled in the composable
            PermissionState.NOT_DETERMINED
        }
    }

    override fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

/**
 * Composable wrapper that handles Android permission requests
 */
@Composable
actual fun rememberCameraPermissionHandler(): CameraPermissionHandler {
    val context = LocalContext.current
    val handler = remember { AndroidCameraPermissionHandler(context) }

    // Track if permission was denied before
    var wasDenied by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            val newState = when {
                isGranted -> PermissionState.GRANTED
                wasDenied -> PermissionState.PERMANENTLY_DENIED  // Denied twice = permanently denied
                else -> {
                    wasDenied = true
                    PermissionState.DENIED
                }
            }
            // Store the callback and call it
            permissionCallbackHolder?.invoke(newState)
        }
    )

    // Create a wrapper handler that uses the launcher
    return remember {
        object : CameraPermissionHandler by handler {
            override fun requestPermission(onResult: (PermissionState) -> Unit) {
                permissionCallbackHolder = onResult
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

// Helper to store callback across recompositions
private var permissionCallbackHolder: ((PermissionState) -> Unit)? = null