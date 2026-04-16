package com.recall.wear.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.recall.app.MainViewModel
import org.koin.mp.KoinPlatform

/**
 * Main activity for Wear OS application
 * Displays flashcard decks in a scrollable list optimized for round screens
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val viewModel = remember { KoinPlatform.getKoin().get<MainViewModel>() }
    DisposableEffect(viewModel) {
        onDispose {
            viewModel.onCleared()
        }
    }

    val decks by viewModel.decks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // TODO 1: Runtime Permission for BODY_SENSORS
    //
    // Instructions:
    // 1. Create a permission launcher using rememberLauncherForActivityResult
    //    with ActivityResultContracts.RequestPermission()
    // 2. Store the permission result in a state variable (e.g., isPermissionGranted)
    // 3. Use LaunchedEffect to request Manifest.permission.BODY_SENSORS on first launch
    //
    // Example:
    //   val permissionLauncher = rememberLauncherForActivityResult(
    //       contract = ActivityResultContracts.RequestPermission()
    //   ) { isGranted ->
    //       // Update your permission state here
    //   }
    //
    //   LaunchedEffect(Unit) {
    //       permissionLauncher.launch(Manifest.permission.BODY_SENSORS)
    //   }

    // TODO 2: Health Services - Heart Rate Monitoring
    //
    // Instructions:
    // 1. Get LocalContext using LocalContext.current
    // 2. Create HealthServicesManager(context)
    // 3. Use produceState to collect heart rate flow ONLY if permission is granted
    // 4. Pass the heart rate value to WearAppContent
    //
    // Example:
    //   val heartRate by produceState<Int?>(initialValue = null) {
    //       if (isPermissionGranted) {
    //           val manager = HealthServicesManager(context)
    //           manager.heartRateFlow().collect { bpm ->
    //               value = bpm
    //           }
    //       }
    //   }
    //
    // Note: You'll also need to implement HealthServicesManager.heartRateFlow()

    // Trigger data loading when the composable enters composition
    LaunchedEffect(Unit) {
        viewModel.fetchDecks()
    }

    WearAppContent(
        decks = decks,
        isLoading = isLoading,
        error = error,
        heartRate = null, // TODO 2: Replace null with actual heart rate state
        onRetry = { viewModel.retry() },
        onDeckClick = { deck ->
            // TODO: Navigate to study screen in future iterations
        }
    )
}
