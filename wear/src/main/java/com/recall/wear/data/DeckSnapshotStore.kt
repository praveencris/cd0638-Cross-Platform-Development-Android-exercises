package com.recall.wear.data

import android.content.Context
import com.recall.app.domain.models.Deck

/**
 * Stores the last deck summary the wearable UI successfully rendered so
 * complication and tile callbacks can read a local snapshot without blocking.
 */
class DeckSnapshotStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSnapshot(decks: List<Deck>) {
        val featuredDeckName = decks.firstOrNull()?.title ?: DEFAULT_DECK_NAME
        val dueCount = decks.sumOf { it.cards.size }

        preferences.edit()
            .putString(KEY_FEATURED_DECK_NAME, featuredDeckName)
            .putInt(KEY_DUE_COUNT, dueCount)
            .apply()
    }

    fun readSnapshot(): DeckSnapshot {
        return DeckSnapshot(
            featuredDeckName = preferences.getString(KEY_FEATURED_DECK_NAME, DEFAULT_DECK_NAME)
                ?: DEFAULT_DECK_NAME,
            dueCount = preferences.getInt(KEY_DUE_COUNT, 0)
        )
    }

    companion object {
        private const val PREFS_NAME = "recall_wear_snapshot"
        private const val KEY_FEATURED_DECK_NAME = "featured_deck_name"
        private const val KEY_DUE_COUNT = "due_count"
        private const val DEFAULT_DECK_NAME = "Recall"
    }
}

data class DeckSnapshot(
    val featuredDeckName: String,
    val dueCount: Int
)
