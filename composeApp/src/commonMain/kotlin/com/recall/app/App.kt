package com.recall.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.KoinContext

/**
 * Shared Compose UI for both Android and iOS
 *
 * TODO: Implement UI that displays deck data from the ViewModel
 */
@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hello KMP! Data missing.",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "TODO: Implement the UI below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // TODO: Step 1 - Inject the MainViewModel using koinInject()
            // val viewModel: MainViewModel = koinInject()

            // TODO: Step 2 - Collect state from the ViewModel
            // val decks by viewModel.decks.collectAsState()
            // val isLoading by viewModel.isLoading.collectAsState()
            // val error by viewModel.error.collectAsState()

            // TODO: Step 3 - Create a LazyColumn to display the list of decks
            // Replace the placeholder Text above with a Column that shows:
            // - A header "Recall (KMP)"
            // - A LazyColumn with cards for each deck
            // - Each card should display: deck.title and "${deck.cards.size} cards"
            // - Handle loading and error states appropriately
        }
    }
}