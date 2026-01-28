package com.recall.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.recall.app.domain.models.Deck

/**
 * Beautiful gradient card component for displaying deck information
 *
 * Features:
 * - Gradient background based on deck color
 * - Icon with rounded background
 * - Due count pill badge
 * - Title and description
 * - Progress bar visualization
 *
 * @param deck The deck data to display
 * @param modifier Optional modifier for the card
 */
@Composable
fun DeckCard(
    deck: Deck,
    modifier: Modifier = Modifier
) {
    val gradient = getGradientColors(deck.colorHex)
    val accentColor = getAccentColor(deck.colorHex)
    val dueCount = calculateDueCount(deck)
    val progress = calculateProgress(deck)
    val description = generateDescription(deck)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradient
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Icon and Due pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with rounded background
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📚",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Due count pill
                    if (dueCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "$dueCount due",
                                style = MaterialTheme.typography.labelSmall,
                                color = accentColor,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Middle section: Title and description
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom section: Progress bar
                Column {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = accentColor,
                        trackColor = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

/**
 * Maps hex color to gradient color scheme
 *
 * Supports three primary color schemes:
 * - Blue (cool tones)
 * - Orange/Red (warm tones)
 * - Emerald/Green (nature tones)
 */
private fun getGradientColors(colorHex: String): List<Color> {
    val color = parseHexColor(colorHex)

    // Extract RGB components as ULong and convert to Float
    val colorValue = color.value
    val red = ((colorValue shr 16) and 0xFFu).toInt() / 255f
    val green = ((colorValue shr 8) and 0xFFu).toInt() / 255f
    val blue = (colorValue and 0xFFu).toInt() / 255f

    return when {
        // Blue tones (high blue, low red)
        blue > red && blue > green -> listOf(
            Color(0xFFEFF6FF), // blue-50
            Color(0xFFDBEAFE)  // blue-100
        )
        // Orange/Red tones (high red)
        red > green && red > blue -> listOf(
            Color(0xFFFFF7ED), // orange-50
            Color(0xFFFFEDD5)  // orange-100
        )
        // Green/Emerald tones (high green)
        green > red && green > blue -> listOf(
            Color(0xFFECFDF5), // emerald-50
            Color(0xFFD1FAE5)  // emerald-100
        )
        // Default to blue
        else -> listOf(
            Color(0xFFEFF6FF),
            Color(0xFFDBEAFE)
        )
    }
}

/**
 * Gets the accent color for icons and text based on hex color
 */
private fun getAccentColor(colorHex: String): Color {
    val color = parseHexColor(colorHex)

    val colorValue = color.value
    val red = ((colorValue shr 16) and 0xFFu).toInt() / 255f
    val green = ((colorValue shr 8) and 0xFFu).toInt() / 255f
    val blue = (colorValue and 0xFFu).toInt() / 255f

    return when {
        blue > red && blue > green -> Color(0xFF3B82F6) // blue-500
        red > green && red > blue -> Color(0xFFF97316)   // orange-500
        green > red && green > blue -> Color(0xFF10B981) // emerald-500
        else -> Color(0xFF3B82F6)
    }
}

/**
 * Parses hex color string to Color object
 *
 * Pure Kotlin implementation that works across all platforms
 */
private fun parseHexColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return try {
        val colorInt = cleanHex.toLong(16)
        if (cleanHex.length == 6) {
            // RGB format, add full alpha
            Color((0xFF000000 or colorInt).toInt())
        } else if (cleanHex.length == 8) {
            // ARGB format
            Color(colorInt.toInt())
        } else {
            Color(0xFF3B82F6) // Default blue
        }
    } catch (e: Exception) {
        Color(0xFF3B82F6) // Default blue
    }
}

/**
 * Calculates the number of cards due for review
 *
 * For now, uses total card count as placeholder
 * In production, would filter cards by due date or interval
 */
private fun calculateDueCount(deck: Deck): Int {
    return deck.cards.size
}

/**
 * Calculates review progress as a percentage
 *
 * For now, uses a simple heuristic based on card count
 * In production, would use actual review history
 */
private fun calculateProgress(deck: Deck): Float {
    if (deck.cards.isEmpty()) return 0f

    // Simple heuristic: progress increases with deck size
    // Smaller decks are assumed to be newer (lower progress)
    // Larger decks are assumed to be more studied (higher progress)
    return when {
        deck.cards.size < 20 -> 0.3f
        deck.cards.size < 50 -> 0.5f
        deck.cards.size < 100 -> 0.7f
        else -> 0.85f
    }
}

/**
 * Generates a description for the deck
 *
 * For now, returns a simple "Vocabulary" placeholder
 * In production, could derive from deck metadata or card content
 */
private fun generateDescription(deck: Deck): String {
    return "Vocabulary • ${deck.cards.size} cards"
}
