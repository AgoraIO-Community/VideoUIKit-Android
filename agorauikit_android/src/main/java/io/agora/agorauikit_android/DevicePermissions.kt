package io.agora.agorauikit_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val PERMISSION_REQ_ID = 22

// Ask for Android device permissions at runtime.
private val REQUESTED_PERMISSIONS = arrayOf<String>(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

/**
 * Request all relevant permissions
 *
 * @param context Activity Context
 * @return True if all the permissions were already granted
 */
@ExperimentalUnsignedTypes
@JvmOverloads public fun AgoraVideoViewer.Companion.requestPermission(context: Context): Boolean {
    return checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) &&
        checkSelfPermission(context, Manifest.permission.CAMERA)
}

@ExperimentalUnsignedTypes
/**
 * Requests a particular permission if not granted
 *
 * @param context Activity Context
 * @param permission Permission String
 * @return True if Permission is granted
 */
@JvmOverloads public fun AgoraVideoViewer.Companion.checkSelfPermission(context: Context, permission: String): Boolean {
    if (ContextCompat.checkSelfPermission(context, permission) !=
        PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            REQUESTED_PERMISSIONS,
            PERMISSION_REQ_ID
        )
        return false
    }
    return true
}
