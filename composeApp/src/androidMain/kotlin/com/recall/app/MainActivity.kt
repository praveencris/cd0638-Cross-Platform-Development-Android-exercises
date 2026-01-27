package com.recall.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val greeting = Greeting().greet()

    // Fetch decks when screen launches
    LaunchedEffect(Unit) {
        viewModel.fetchDecks()
    }

    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (uiState) {
                    is MainViewModel.UiState.Initial -> {
                        Text("Ready to fetch decks")
                    }
                    is MainViewModel.UiState.Loading -> {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading decks...",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    is MainViewModel.UiState.Success -> {
                        val deckCount = (uiState as MainViewModel.UiState.Success).deckCount
                        Text(
                            text = "$deckCount Decks Available",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is MainViewModel.UiState.Error -> {
                        val errorMessage = (uiState as MainViewModel.UiState.Error).message
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.fetchDecks() },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}