package com.recall.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import com.recall.app.MainViewModel
import com.recall.wear.data.DeckSnapshotStore
import com.recall.wear.data.HealthServicesManager
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

    // Wrap content with permission handler for heart rate access
    HeartRatePermissionHandler {
        WearAppWithPermission(viewModel)
    }
}

@Composable
private fun WearAppWithPermission(viewModel: MainViewModel) {
    val decks by viewModel.decks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val healthServicesManager = remember { KoinPlatform.getKoin().get<HealthServicesManager>() }
    val snapshotStore = remember { KoinPlatform.getKoin().get<DeckSnapshotStore>() }

    // Observe heart rate from Health Services
    // Only called after permission is granted by HeartRatePermissionHandler
    val heartRate by produceState<Int?>(initialValue = null) {
        healthServicesManager.heartRateFlow().collect { bpm ->
            value = bpm
        }
    }

    LaunchedEffect(decks) {
        snapshotStore.saveSnapshot(decks)
    }

    // Trigger data loading when the composable enters composition
    LaunchedEffect(Unit) {
        viewModel.fetchDecks()
    }

    WearAppContent(
        decks = decks,
        isLoading = isLoading,
        error = error,
        heartRate = heartRate,
        onRetry = { viewModel.retry() },
        onDeckClick = { deck ->
            // TODO: Navigate to study screen in future iterations
        }
    )
}
