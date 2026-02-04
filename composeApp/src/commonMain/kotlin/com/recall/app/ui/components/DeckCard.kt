package com.recall.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recall.app.domain.models.Deck
import com.recall.app.domain.models.DeckCategory
import com.recall.app.ui.WindowSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import recall.composeapp.generated.resources.Res
import recall.composeapp.generated.resources.biotech_24px
import recall.composeapp.generated.resources.cards_count
import recall.composeapp.generated.resources.cards_due
import recall.composeapp.generated.resources.history_edu_24px
import recall.composeapp.generated.resources.label_open_deck
import recall.composeapp.generated.resources.language_chinese_dayi_24px
import recall.composeapp.generated.resources.logo_dev_24px

/**
 * Color palette for deck cards
 *
 * Each palette defines gradient colors and an accent color for consistent theming
 */
private data class ColorPalette(
    val gradientStart: Color,
    val gradientEnd: Color,
    val accent: Color
)

/**
 * Predefined color palettes matching the Zen Study design
 */
private val colorPalettes = listOf(
    // Blue palette
    ColorPalette(
        gradientStart = Color(0xFFEFF6FF), // blue-50
        gradientEnd = Color(0xFFDBEAFE),   // blue-100
        accent = Color(0xFF2B8CEE)         // primary blue
    ),
    // Orange palette
    ColorPalette(
        gradientStart = Color(0xFFFFF7ED), // orange-50
        gradientEnd = Color(0xFFFFEDD5),   // orange-100
        accent = Color(0xFFF97316)         // orange-500
    ),
    // Emerald palette
    ColorPalette(
        gradientStart = Color(0xFFECFDF5), // emerald-50
        gradientEnd = Color(0xFFD1FAE5),   // emerald-100
        accent = Color(0xFF10B981)         // emerald-500
    )
)

/**
 * Maps deck category to the appropriate icon resource
 */
private fun getCategoryIcon(category: DeckCategory): DrawableResource {
    return when (category) {
        DeckCategory.LANGUAGE -> Res.drawable.language_chinese_dayi_24px
        DeckCategory.HISTORY -> Res.drawable.history_edu_24px
        DeckCategory.SCIENCE -> Res.drawable.biotech_24px
        DeckCategory.TECHNOLOGY -> Res.drawable.logo_dev_24px
    }
}

/**
 * Beautiful gradient card component for displaying deck information
 *
 * Features:
 * - Gradient background from rotating color palette
 * - Icon with rounded background
 * - Due count pill badge
 * - Title and description
 * - Progress bar visualization
 * - Adaptive sizing based on window size
 *
 * @param deck The deck data to display
 * @param index The deck's position in the list (for color palette rotation)
 * @param windowSize Current window size for adaptive layout
 * @param onClick Callback when the card is clicked
 * @param modifier Optional modifier for the card
 */
@Composable
fun DeckCard(
    deck: Deck,
    index: Int,
    windowSize: WindowSize,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colorPalette = colorPalettes[index % colorPalettes.size]
    val dueCount = calculateDueCount(deck)
    val progress = deck.progress
    val description = generateDescription(deck)

    // Adaptive card height based on window size
    val cardHeight = when (windowSize) {
        WindowSize.Compact -> 200.dp
        WindowSize.Medium -> 220.dp
        WindowSize.Expanded -> 240.dp
    }

    // TODO 1: Add Modifier.semantics(mergeDescendants = true) to group all card elements
    // TODO 2: Add contentDescription that summarizes: title, card count, and progress
    // TODO 3: Add onClick(label = stringResource(Res.string.label_open_deck), action = null)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1F2937) // Default dark text color
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorPalette.gradientStart,
                            colorPalette.gradientEnd
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
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
                        Icon(
                            painter = painterResource(getCategoryIcon(deck.category)),
                            contentDescription = "${deck.category.name} icon",
                            tint = colorPalette.accent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Due count pill
                    if (dueCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = stringResource(Res.string.cards_due, dueCount),
                                style = MaterialTheme.typography.labelMedium,
                                color = colorPalette.accent,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Middle section: Title and description
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = deck.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1F2937),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
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
                        color = colorPalette.accent,
                        trackColor = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
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
 * Generates a description for the deck
 *
 * For now, returns a simple "Vocabulary" placeholder
 * In production, could derive from deck metadata or card content
 */
@Composable
private fun generateDescription(deck: Deck): String {
    val cardCountText = pluralStringResource(Res.plurals.cards_count, deck.cards.size, deck.cards.size)
    return "Vocabulary • $cardCountText"
}
