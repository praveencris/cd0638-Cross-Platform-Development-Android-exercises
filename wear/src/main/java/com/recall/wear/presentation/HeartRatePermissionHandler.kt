package com.recall.wear.presentation

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Handles runtime permission request for heart rate access
 *
 * On Wear OS 6+ (API 36+), requires android.permission.health.READ_HEART_RATE
 * On Wear OS 5.1 and below (API ≤35), requires android.permission.BODY_SENSORS
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HeartRatePermissionHandler(
    content: @Composable () -> Unit
) {
    // Determine which permission to request based on API level
    val permission = if (Build.VERSION.SDK_INT >= 36) {
        "android.permission.health.READ_HEART_RATE"
    } else {
        Manifest.permission.BODY_SENSORS
    }

    val permissionState = rememberPermissionState(permission)

    when {
        permissionState.status.isGranted -> {
            // Permission granted - show content with heart rate
            content()
        }
        permissionState.status.shouldShowRationale -> {
            // Permission denied previously - show rationale
            PermissionRationale(
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        }
        else -> {
            // Initial state - request permission
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
            // Show loading while waiting for permission dialog
            PermissionLoading()
        }
    }
}

@Composable
private fun PermissionRationale(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Heart rate access is needed to display your heart rate during study sessions.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
        Spacer(modifier = Modifier.height(8.dp))
        Chip(
            label = { Text("Grant Permission") },
            onClick = onRequestPermission,
            colors = ChipDefaults.primaryChipColors()
        )
    }
}

@Composable
private fun PermissionLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
