package com.recall.wear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.recall.app.MainViewModel
import org.koin.android.ext.android.inject

/**
 * Main activity for Wear OS application
 * Displays flashcard decks in a scrollable list optimized for round screens
 */
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp(viewModel = viewModel)
        }
    }
}

@Composable
fun WearApp(viewModel: MainViewModel) {
    val decks by viewModel.decks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    WearAppContent(
        decks = decks,
        isLoading = isLoading,
        error = error,
        onRetry = { viewModel.retry() },
        onDeckClick = { deck ->
            // TODO: Navigate to study screen in future iterations
        }
    )
}
