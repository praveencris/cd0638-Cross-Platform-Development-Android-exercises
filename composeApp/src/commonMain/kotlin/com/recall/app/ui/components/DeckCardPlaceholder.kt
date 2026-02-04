package com.recall.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.recall.app.ui.WindowSize

/**
 * Shimmer loading placeholder for DeckCard
 *
 * Displays an animated shimmer effect while deck data is loading from the database.
 * Matches the exact layout and dimensions of DeckCard for a seamless loading experience.
 *
 * Performance benefits:
 * - Shows UI structure immediately (no blank screen)
 * - Indicates loading state without blocking interaction
 * - Smooth transition when real data loads
 *
 * UX benefits:
 * - Reduces perceived loading time
 * - Maintains visual consistency
 * - Professional, polished appearance
 *
 * @param windowSize Current window size for adaptive layout
 * @param modifier Optional modifier for the card
 */
@Composable
fun DeckCardPlaceholder(
    windowSize: WindowSize,
    modifier: Modifier = Modifier
) {
    // Match DeckCard's adaptive height
    val cardHeight = when (windowSize) {
        WindowSize.Compact -> 200.dp
        WindowSize.Medium -> 220.dp
        WindowSize.Expanded -> 240.dp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5) // Light gray background
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                // Top section: Icon placeholder and due pill placeholder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon placeholder (circular)
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )

                    // Due pill placeholder
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shimmerEffect()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Middle section: Title and description placeholders
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title placeholder (2 lines)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description placeholder (2 lines)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom section: Progress bar placeholder
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
