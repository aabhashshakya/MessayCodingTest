package jp.co.ndk_group.messay_duck_hunt.core.utils

/// Created by Aabhash Shakya on 15/02/2026
import androidx.compose.runtime.*

/**
 * Camera permission states
 */
enum class PermissionState {
    NOT_DETERMINED,  // Initial state, not yet requested
    GRANTED,         // Permission granted
    DENIED,          // Permission denied
    PERMANENTLY_DENIED  // Permission permanently denied (only Android)
}

/**
 * Interface for platform-specific camera permission handling
 */
interface CameraPermissionHandler {
    /**
     * Check if camera permission is currently granted
     */
    fun isPermissionGranted(): Boolean

    /**
     * Request camera permission from the user
     */
    fun requestPermission(onResult: (PermissionState) -> Unit)

    /**
     * Get current permission state
     */
    fun getPermissionState(): PermissionState

    /**
     * Open app settings (useful when permission is permanently denied)
     */
    fun openAppSettings()
}

/**
 * Composable function to handle camera permission with reactive state
 */
@Composable
expect fun rememberCameraPermissionHandler(): CameraPermissionHandler

/**
 * Composable state holder for camera permission
 */
@Composable
fun rememberCameraPermissionState(): CameraPermissionState {
    val handler = rememberCameraPermissionHandler()
    return remember { CameraPermissionState(handler) }
}

/**
 * State holder for camera permission that provides reactive updates
 */
class CameraPermissionState(
    private val handler: CameraPermissionHandler
) {
    private val _permissionState = mutableStateOf(handler.getPermissionState())
    val permissionState: State<PermissionState> = _permissionState

    val isGranted: Boolean
        get() = _permissionState.value == PermissionState.GRANTED

    val isPermanentlyDenied: Boolean
        get() = _permissionState.value == PermissionState.PERMANENTLY_DENIED

    /**
     * Request camera permission
     */
    fun requestPermission(onResult: (PermissionState) -> Unit = {}) {
        handler.requestPermission { newState ->
            _permissionState.value = newState
            onResult(newState)
        }
    }

    /**
     * Refresh permission state (useful after returning from settings)
     */
    fun refreshPermissionState() {
        _permissionState.value = handler.getPermissionState()
    }

    /**
     * Open app settings
     */
    fun openAppSettings() {
        handler.openAppSettings()
    }
}