package com.recall.app.network

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests for RecallApiService
 * Tests the actual API call and JSON parsing
 */
class RecallApiTest {

    /**
     * Test that the API successfully fetches and parses decks
     */
    @Test
    fun test_fetchAllDecks_returns_decks() = runTest {
        // Arrange: Create the API service with the configured client
        val apiService = RecallApiService(NetworkClient.httpClient)

        // Act: Fetch all decks from the API
        val decks = apiService.fetchAllDecks()

        // Assert: Verify we got decks back
        assertTrue(
            actual = decks.isNotEmpty(),
            message = "Should fetch at least one deck"
        )
    }

    /**
     * Test that the first deck has the expected title
     */
    @Test
    fun test_first_deck_title_is_nasa_space_gallery() = runTest {
        // Arrange
        val apiService = RecallApiService(NetworkClient.httpClient)

        // Act
        val decks = apiService.fetchAllDecks()

        // Assert: Verify the first deck's title
        val firstDeck = decks.firstOrNull()
        assertNotNull(firstDeck, "First deck should not be null")
        assertEquals(
            expected = "NASA Space Gallery",
            actual = firstDeck.title,
            message = "First deck title should be 'NASA Space Gallery'"
        )
    }

    /**
     * Test that decks contain cards with proper data
     */
    @Test
    fun test_decks_contain_cards_with_data() = runTest {
        // Arrange
        val apiService = RecallApiService(NetworkClient.httpClient)

        // Act
        val decks = apiService.fetchAllDecks()

        // Assert: Verify first deck has cards
        val firstDeck = decks.first()
        assertTrue(
            actual = firstDeck.cards.isNotEmpty(),
            message = "First deck should contain cards"
        )

        // Verify card structure
        val firstCard = firstDeck.cards.first()
        assertNotNull(firstCard.id, "Card should have an ID")
        assertNotNull(firstCard.front, "Card should have front text")
        assertNotNull(firstCard.back, "Card should have back text")
    }

    /**
     * Test that color field is properly mapped to colorHex
     */
    @Test
    fun test_deck_color_is_mapped_correctly() = runTest {
        // Arrange
        val apiService = RecallApiService(NetworkClient.httpClient)

        // Act
        val decks = apiService.fetchAllDecks()

        // Assert: Verify color field is mapped
        val firstDeck = decks.first()
        assertTrue(
            actual = firstDeck.colorHex.startsWith("#"),
            message = "Deck color should be a hex color code starting with #"
        )
    }
}
