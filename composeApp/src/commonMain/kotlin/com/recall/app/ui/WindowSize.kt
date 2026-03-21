package com.recall.app.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Represents the window size categories for adaptive layouts.
 *
 * This enum is used to determine which layout variant should be displayed
 * based on the available screen width, following Material Design 3 breakpoints.
 */
enum class WindowSize {
    /**
     * Compact layout for phones and small devices.
     * Typically used for widths less than 600dp.
     */
    Compact,

    /**
     * Medium layout for small tablets and unfolded foldables.
     * Typically used for widths between 600dp and 840dp.
     */
    Medium,

    /**
     * Expanded layout for large tablets and desktop screens.
     * Typically used for widths 840dp and above.
     */
    Expanded
}

/**
 * Provides adaptive layout support by calculating the current window size category.
 *
 * This composable uses [BoxWithConstraints] to determine the available screen width
 * and maps it to one of three window size categories following Material Design 3
 * breakpoint recommendations:
 *
 * - **Compact**: width < 600dp (phones, portrait mode)
 * - **Medium**: 600dp ≤ width < 840dp (small tablets, large phones in landscape)
 * - **Expanded**: width ≥ 840dp (large tablets, desktop)
 *
 * The window size is recalculated automatically when the screen is rotated or
 * the window is resized, allowing for truly adaptive layouts across platforms.
 *
 * @param content The composable content that receives the current [WindowSize]
 */
@Composable
fun AdaptiveLayout(content: @Composable (WindowSize) -> Unit) {
    BoxWithConstraints {
        val windowSize = when {
            maxWidth < 600.dp -> WindowSize.Compact
            maxWidth < 840.dp -> WindowSize.Medium
            // Phones in landscape: wide enough for Expanded but height is too
            // constrained for two-pane layouts (Google MD3 guideline)
            maxHeight < 480.dp -> WindowSize.Medium
            else -> WindowSize.Expanded
        }
        content(windowSize)
    }
}
