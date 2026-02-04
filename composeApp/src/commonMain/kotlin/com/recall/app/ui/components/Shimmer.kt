package com.recall.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Shimmer loading effect modifier
 *
 * Creates an animated gradient that moves from left to right, giving the appearance
 * of a "shimmering" loading placeholder. Commonly used in modern apps to indicate
 * content is loading while maintaining UI structure.
 *
 * Usage:
 * ```
 * Box(
 *     modifier = Modifier
 *         .size(200.dp, 20.dp)
 *         .shimmerEffect()
 * )
 * ```
 *
 * Performance:
 * - Uses `rememberInfiniteTransition` for efficient animation
 * - Hardware accelerated on most platforms
 * - Minimal CPU/battery impact
 *
 * @return Modifier with animated shimmer background
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    // Colors for the shimmer gradient
    val shimmerColors = listOf(
        Color(0xFFE0E0E0), // Light gray
        Color(0xFFF5F5F5), // Very light gray (shimmer highlight)
        Color(0xFFE0E0E0)  // Light gray
    )

    // Infinite transition for continuous animation
    val transition = rememberInfiniteTransition(label = "shimmer")

    // Animate the translation offset from 0f to 1000f
    // This creates the left-to-right sliding effect
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500, // 1.5 seconds for smooth effect
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    // Create linear gradient brush that moves with animation
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 200f, translateAnimation + 200f)
    )

    // Apply gradient as background
    this.background(brush)
}
