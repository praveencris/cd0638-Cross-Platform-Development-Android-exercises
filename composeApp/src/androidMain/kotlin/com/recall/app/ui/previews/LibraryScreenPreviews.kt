package com.recall.app.ui.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.recall.app.ui.LibraryScreen
import com.recall.app.ui.WindowSize

/**
 * Preview functions for LibraryScreen component
 *
 * Demonstrates responsive layouts for different WindowSize categories:
 * - Compact: Single column (phone portrait)
 * - Medium: 2-column grid (small tablet)
 * - Expanded: 3-column grid (large tablet/desktop)
 */

@Preview(
    name = "Compact Layout (Phone)",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun LibraryScreenCompactPreview() {
    LibraryScreen(
        windowSize = WindowSize.Compact,
        decks = sampleDeckList()
    )
}

@Preview(
    name = "Medium Layout (Small Tablet)",
    showBackground = true,
    widthDp = 700,
    heightDp = 500
)
@Composable
private fun LibraryScreenMediumPreview() {
    LibraryScreen(
        windowSize = WindowSize.Medium,
        decks = sampleDeckList()
    )
}

@Preview(
    name = "Expanded Layout (Large Tablet)",
    showBackground = true,
    widthDp = 1024,
    heightDp = 768
)
@Composable
private fun LibraryScreenExpandedPreview() {
    LibraryScreen(
        windowSize = WindowSize.Expanded,
        decks = sampleDeckList()
    )
}

@Preview(
    name = "Compact with Few Decks",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
private fun LibraryScreenCompactFewDecksPreview() {
    LibraryScreen(
        windowSize = WindowSize.Compact,
        decks = sampleDeckList().take(3)
    )
}

@Preview(
    name = "Medium with Few Decks",
    showBackground = true,
    widthDp = 700,
    heightDp = 500
)
@Composable
private fun LibraryScreenMediumFewDecksPreview() {
    LibraryScreen(
        windowSize = WindowSize.Medium,
        decks = sampleDeckList().take(3)
    )
}

@Preview(
    name = "Expanded Landscape (Desktop)",
    showBackground = true,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun LibraryScreenExpandedLandscapePreview() {
    LibraryScreen(
        windowSize = WindowSize.Expanded,
        decks = sampleDeckList()
    )
}
