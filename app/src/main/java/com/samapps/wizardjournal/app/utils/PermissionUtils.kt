package com.samapps.wizardjournal.app.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState


@ExperimentalPermissionsApi
@Composable
fun rememberCustomPermissionState(
    permission: String,
    onPermissionGranted: () -> Unit = {},
    onPermissionDenied: () -> Unit = {},
    onPermissionPermanentlyDenied: () -> Unit = {}
): PermissionHandler {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission)

    LaunchedEffect(permissionState.status) {
        when (permissionState.status) {
            PermissionStatus.Granted -> onPermissionGranted()
            is PermissionStatus.Denied -> {
                if ((permissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    onPermissionDenied()
                } else {
                    onPermissionPermanentlyDenied()
                }
            }
        }
    }

    return rememberPermissionHandler(
        permissionState = permissionState,
        context = context
    )
}

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionHandler(
    permissionState: PermissionState,
    context: android.content.Context
): PermissionHandler {
    var askedForPermissions by remember { mutableStateOf(false) }
    println("Asked for permissions: $askedForPermissions")
    return PermissionHandler(
        isGranted = permissionState.status == PermissionStatus.Granted,
        isPermanentlyDenied = askedForPermissions && permissionState.status is PermissionStatus.Denied && !(permissionState.status as PermissionStatus.Denied).shouldShowRationale,
        requestPermission = { askedForPermissions = true; permissionState.launchPermissionRequest() },
        openAppSettings = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        },
    )
}

data class PermissionHandler(
    val isGranted: Boolean,
    val isPermanentlyDenied: Boolean,
    val requestPermission: () -> Unit,
    val openAppSettings: () -> Unit,
)