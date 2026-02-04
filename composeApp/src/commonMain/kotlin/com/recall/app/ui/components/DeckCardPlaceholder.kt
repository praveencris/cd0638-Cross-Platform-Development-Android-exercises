package com.recall.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.recall.app.ui.WindowSize

/**
 * Shimmer loading placeholder for DeckCard
 *
 * TODO (Optional Bonus): Build a skeleton card using Box and shimmerEffect()
 *
 * Instructions:
 * 1. Create a Card with the same dimensions as DeckCard
 * 2. Use Box composables to represent the icon, title, description, and progress bar
 * 3. Apply .shimmerEffect() modifier to each Box
 * 4. Match the exact layout of DeckCard to prevent layout shifts
 *
 * Hint: Use the same cardHeight logic as DeckCard based on windowSize
 */
@Composable
fun DeckCardPlaceholder(
    windowSize: WindowSize,
    modifier: Modifier = Modifier
) {
    // TODO: Implement shimmer placeholder
    // The Shimmer.kt helper is already provided for you
}
