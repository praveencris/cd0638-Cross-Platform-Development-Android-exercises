package com.recall.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeckEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,  // Deck category (LANGUAGE, HISTORY, SCIENCE, TECHNOLOGY)
    val currentCardIndex: Int = 0  // Track progress for resume functionality
)
