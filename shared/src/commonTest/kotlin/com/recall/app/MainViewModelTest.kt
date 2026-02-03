package com.recall.app

import com.recall.app.domain.models.Card
import com.recall.app.domain.models.Deck
import com.recall.app.network.RecallApiService
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for MainViewModel
 *
 * Tests state management, business logic, and API integration
 * without requiring Android framework dependencies.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: MainViewModel
    private lateinit var fakeApiService: FakeRecallApiService

    @BeforeTest
    fun setup() {
        // Set main dispatcher for coroutine testing
        Dispatchers.setMain(testDispatcher)

        // Create fake API service with test data
        fakeApiService = FakeRecallApiService()

        // Create ViewModel with fake service
        viewModel = MainViewModel(fakeApiService)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== State Management Tests ====================

    @Test
    fun test_initialState_isEmpty() = runTest {
        // Assert initial states
        assertEquals(emptyList(), viewModel.decks.value, "Decks should be empty initially")
        assertFalse(viewModel.isLoading.value, "Should not be loading initially")
        assertNull(viewModel.error.value, "Should have no error initially")
    }

    @Test
    fun test_fetchDecks_setsLoadingState() = runTest {
        // Act
        viewModel.fetchDecks()

        // Complete the coroutine
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert loading state is cleared after fetch completes
        assertFalse(viewModel.isLoading.value, "Should not be loading after fetch completes")
    }

    @Test
    fun test_fetchDecks_success_updatesDecks() = runTest {
        // Arrange
        val expectedDecks = listOf(
            Deck(
                id = "deck1",
                title = "Test Deck 1",
                colorHex = "#FF5722",
                cards = listOf(
                    Card(id = "card1", front = "Q1", back = "A1")
                )
            )
        )
        fakeApiService.decksToReturn = expectedDecks

        // Act
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(expectedDecks, viewModel.decks.value, "Decks should be updated after successful fetch")
        assertNull(viewModel.error.value, "Should have no error after successful fetch")
    }

    @Test
    fun test_fetchDecks_failure_setsError() = runTest {
        // Arrange
        val errorMessage = "Network error"
        fakeApiService.shouldThrowError = true
        fakeApiService.errorMessage = errorMessage

        // Act
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(errorMessage, viewModel.error.value, "Error message should be set after failed fetch")
        assertEquals(emptyList(), viewModel.decks.value, "Decks should remain empty after failed fetch")
    }

    @Test
    fun test_retry_clearsError() = runTest {
        // Arrange - set error first
        fakeApiService.shouldThrowError = true
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.error.value, "Error should be set initially")

        // Change fake to succeed
        fakeApiService.shouldThrowError = false

        // Act
        viewModel.retry()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertNull(viewModel.error.value, "Error should be cleared after successful retry")
    }

    // ==================== Progress Tracking Tests ====================

    @Test
    fun test_onCardReviewed_updatesProgress() = runTest {
        // Arrange - load deck first
        val testDeck = Deck(
            id = "deck1",
            title = "Test Deck",
            colorHex = "#FF0000",
            cards = listOf(
                Card(id = "card1", front = "Q1", back = "A1"),
                Card(id = "card2", front = "Q2", back = "A2")
            )
        )
        fakeApiService.decksToReturn = listOf(testDeck)
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - review first card (move to index 1)
        viewModel.onCardReviewed("deck1", 1)

        // Assert - use public getStartIndex() to verify progress
        val progress = viewModel.getStartIndex("deck1")
        assertEquals(1, progress, "Progress should be 1 after reviewing first card")
    }

    @Test
    fun test_onCardReviewed_incrementsProgressSequentially() = runTest {
        // Arrange
        val testDeck = Deck(
            id = "deck1",
            title = "Test",
            colorHex = "#00FF00",
            cards = List(5) { index ->
                Card(id = "card$index", front = "Q$index", back = "A$index")
            }
        )
        fakeApiService.decksToReturn = listOf(testDeck)
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - review cards sequentially
        viewModel.onCardReviewed("deck1", 1)
        assertEquals(1, viewModel.getStartIndex("deck1"), "Progress should be 1 after first card")

        viewModel.onCardReviewed("deck1", 2)
        assertEquals(2, viewModel.getStartIndex("deck1"), "Progress should be 2 after second card")

        viewModel.onCardReviewed("deck1", 3)
        assertEquals(3, viewModel.getStartIndex("deck1"), "Progress should be 3 after third card")
    }

    @Test
    fun test_onCardReviewed_handlesMultipleDecks() = runTest {
        // Arrange - multiple decks
        val decks = listOf(
            Deck(
                id = "deck1",
                title = "Deck 1",
                colorHex = "#0000FF",
                cards = listOf(
                    Card(id = "c1", front = "Q1", back = "A1"),
                    Card(id = "c1b", front = "Q1b", back = "A1b")
                )
            ),
            Deck(
                id = "deck2",
                title = "Deck 2",
                colorHex = "#FF00FF",
                cards = listOf(
                    Card(id = "c2", front = "Q2", back = "A2"),
                    Card(id = "c2b", front = "Q2b", back = "A2b")
                )
            )
        )
        fakeApiService.decksToReturn = decks
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - review cards from different decks
        viewModel.onCardReviewed("deck1", 1)
        viewModel.onCardReviewed("deck2", 1)

        // Assert
        assertEquals(1, viewModel.getStartIndex("deck1"), "Deck 1 progress should be 1")
        assertEquals(1, viewModel.getStartIndex("deck2"), "Deck 2 progress should be 1")
    }

    // ==================== Resume Functionality Tests ====================

    @Test
    fun test_getStartIndex_returnsZeroForNewDeck() {
        // Act
        val startIndex = viewModel.getStartIndex("new_deck")

        // Assert
        assertEquals(0, startIndex, "Should start at index 0 for new deck")
    }

    @Test
    fun test_getStartIndex_returnsProgressForExistingDeck() = runTest {
        // Arrange
        val testDeck = Deck(
            id = "deck1",
            title = "Test",
            colorHex = "#FFFF00",
            cards = List(5) { Card(id = "c$it", front = "Q$it", back = "A$it") }
        )
        fakeApiService.decksToReturn = listOf(testDeck)
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Review 3 cards
        viewModel.onCardReviewed("deck1", 1)
        viewModel.onCardReviewed("deck1", 2)
        viewModel.onCardReviewed("deck1", 3)

        // Act
        val startIndex = viewModel.getStartIndex("deck1")

        // Assert
        assertEquals(3, startIndex, "Should resume from index 3 after reviewing 3 cards")
    }

    @Test
    fun test_getProgressPercentage_calculatesCorrectly() = runTest {
        // Arrange
        val testDeck = Deck(
            id = "deck1",
            title = "Test",
            colorHex = "#00FFFF",
            cards = List(10) { Card(id = "c$it", front = "Q$it", back = "A$it") }
        )
        fakeApiService.decksToReturn = listOf(testDeck)
        viewModel.fetchDecks()
        testDispatcher.scheduler.advanceUntilIdle()

        // Review 5 out of 10 cards (move to index 5)
        viewModel.onCardReviewed("deck1", 5)

        // Act
        val deck = viewModel.decks.value.first()
        val progress = viewModel.getStartIndex("deck1")
        val percentage = (progress.toFloat() / deck.cards.size * 100).toInt()

        // Assert
        assertEquals(50, percentage, "Progress should be 50% after reviewing 5 of 10 cards")
    }
}

/**
 * Fake implementation of RecallApiService for testing
 * Allows controlled responses without network calls
 */
class FakeRecallApiService : RecallApiService(
    HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                respond(
                    content = ByteReadChannel("{}"),
                    status = HttpStatusCode.OK,
                    headers = headersOf("Content-Type", "application/json")
                )
            }
        }
    }
) {
    var decksToReturn: List<Deck> = emptyList()
    var shouldThrowError: Boolean = false
    var errorMessage: String = "Test error"

    override suspend fun fetchAllDecks(): List<Deck> {
        if (shouldThrowError) {
            throw Exception(errorMessage)
        }
        return decksToReturn
    }
}
