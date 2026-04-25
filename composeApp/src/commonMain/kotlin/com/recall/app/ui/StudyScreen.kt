package com.recall.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.recall.app.domain.models.Card
import com.recall.app.domain.models.Deck

/**
 * Study screen for reviewing flashcards with spaced repetition
 *
 * Features:
 * - Card flip interaction (tap to reveal answer)
 * - Progress tracking (current card / total cards)
 * - Spaced repetition buttons (Again, Hard, Good, Easy)
 * - Adaptive background gradient based on deck category
 *
 * @param deck The deck being studied
 * @param onClose Callback when user closes the study session
 * @param modifier Optional modifier for the screen
 */
@Composable
fun StudyScreen(
    deck: Deck,
    startIndex: Int = 0,
    onCardReviewed: (Int) -> Unit = {},
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentCardIndex by remember { mutableStateOf(startIndex) }
    var isFlipped by remember { mutableStateOf(false) }
    val totalCards = deck.cards.size
    val progress = if (totalCards > 0) {
        (currentCardIndex + 1).toFloat() / totalCards.toFloat()
    } else {
        0f
    }
    // Production guard: an empty deck should display "0 / 0" instead of the
    // misleading "1 / 0" that a simple currentCardIndex + 1 would produce.
    val displayedCardIndex = if (totalCards > 0) currentCardIndex + 1 else 0

    val currentCard = if (totalCards > 0 && currentCardIndex < totalCards) {
        deck.cards[currentCardIndex]
    } else {
        null
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar
            TopBar(
                deckTitle = deck.title,
                currentIndex = displayedCardIndex,
                totalCards = totalCards,
                progress = progress,
                onClose = onClose
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card area
            if (currentCard != null) {
                FlashCard(
                    card = currentCard,
                    deck = deck,
                    isFlipped = isFlipped,
                    onCardTap = { isFlipped = !isFlipped },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Spaced repetition buttons
                if (isFlipped) {
                    SpacedRepetitionButtons(
                        onAgain = {
                            // Move to next card
                            currentCardIndex = (currentCardIndex + 1) % totalCards
                            isFlipped = false
                        },
                        onHard = {
                            currentCardIndex = (currentCardIndex + 1) % totalCards
                            isFlipped = false
                        },
                        onGood = {
                            currentCardIndex = (currentCardIndex + 1) % totalCards
                            isFlipped = false
                        },
                        onEasy = {
                            currentCardIndex = (currentCardIndex + 1) % totalCards
                            isFlipped = false
                        },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No cards in this deck",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * Top bar with close button, deck title, and progress
 */
@Composable
private fun TopBar(
    deckTitle: String,
    currentIndex: Int,
    totalCards: Int,
    progress: Float,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(40.dp)
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF1F2937),
                    fontWeight = FontWeight.Normal
                )
            }

            // Deck title
            Text(
                text = deckTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )

            // Progress counter
            Text(
                text = "$currentIndex / $totalCards",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF6B7280)
            )
        }

        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .padding(horizontal = 20.dp),
            color = Color(0xFF2B8CEE),
            trackColor = Color(0xFFE5E7EB)
        )
    }
}

/**
 * Flashcard component with flip interaction
 */
@Composable
private fun FlashCard(
    card: Card,
    deck: Deck,
    isFlipped: Boolean,
    onCardTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onCardTap),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top gradient section (teal background)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.4f)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF14B8A6),
                                    Color(0xFF0D9488)
                                )
                            )
                        ),
                    contentAlignment = Alignment.TopEnd
                ) {
                    // Category badge
                    Surface(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = deck.category.name.replace("_", " "),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                // Bottom white section (question/answer)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.6f)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        // Label
                        Text(
                            text = if (isFlipped) "ANSWER" else "QUESTION",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isFlipped) Color(0xFF10B981) else Color(0xFF2B8CEE),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Question/Answer text
                        Text(
                            text = if (isFlipped) card.back else card.front,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937),
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.headlineMedium.lineHeight
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tap to flip hint (only show when not flipped)
                        if (!isFlipped) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "↻",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color(0xFF9CA3AF)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tap to flip",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color(0xFF9CA3AF)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Spaced repetition buttons with gradient backgrounds
 */
@Composable
private fun SpacedRepetitionButtons(
    onAgain: () -> Unit,
    onHard: () -> Unit,
    onGood: () -> Unit,
    onEasy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Again button (Red)
        RepetitionButton(
            label = "Again",
            interval = "1m",
            gradientColors = listOf(Color(0xFFFEE2E2), Color(0xFFFECDD3)),
            textColor = Color(0xFFDC2626),
            onClick = onAgain,
            modifier = Modifier.weight(1f)
        )

        // Hard button (Orange)
        RepetitionButton(
            label = "Hard",
            interval = "2d",
            gradientColors = listOf(Color(0xFFFED7AA), Color(0xFFFDBA74)),
            textColor = Color(0xFFEA580C),
            onClick = onHard,
            modifier = Modifier.weight(1f)
        )

        // Good button (Green)
        RepetitionButton(
            label = "Good",
            interval = "3d",
            gradientColors = listOf(Color(0xFFBBF7D0), Color(0xFF86EFAC)),
            textColor = Color(0xFF16A34A),
            onClick = onGood,
            modifier = Modifier.weight(1f)
        )

        // Easy button (Blue)
        RepetitionButton(
            label = "Easy",
            interval = "4d",
            gradientColors = listOf(Color(0xFFBFDBFE), Color(0xFF93C5FD)),
            textColor = Color(0xFF2563EB),
            onClick = onEasy,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual spaced repetition button
 */
@Composable
private fun RepetitionButton(
    label: String,
    interval: String,
    gradientColors: List<Color>,
    textColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = interval,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}
