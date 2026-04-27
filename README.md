# 🏁 Module 3 Solution: Networking with Ktor

**Branch:** `sol-lesson-3-networking-with-ktor`

This branch contains the fully implemented networking layer for the Recall app. The core focus was transitioning from static data to a dynamic, remote data source using **Ktor** and **Kotlinx Serialization**.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

### 1. Multiplatform Network Configuration

The network stack is configured to use platform-optimal engines while keeping the implementation logic in `commonMain`:

* **Android:** Utilizes the `OkHttp` engine for robust connection pooling and interception.
* **iOS:** Utilizes the `Darwin` engine to leverage native `NSURLSession` for better performance and system integration.

### 2. Global HTTP Client

The `NetworkClient` object initializes a shared `HttpClient` with the following features:

* **ContentNegotiation:** Automatically handles JSON-to-Kotlin object conversion.
* **Kotlinx Serialization:** Configured to ignore unknown JSON keys to ensure the app remains resilient to API changes or extra metadata in the Gist response.

### 3. API Service Layer

The `RecallApiService` acts as the data provider. It is hardcoded to point to the project's primary data source:

* **Endpoint:** `https://gist.githubusercontent.com/kshitizbali/7dba0064f63c3971d0194a6b5d938d56/raw/4b138fbc16292c86d2ada848738b0a706c29d5cc/decks.json`
* **Mapping:** The service parses the raw JSON into the `List<Deck>` model defined in Module 2.

## 📂 Key Files

| File Path | Description |
| --- | --- |
| `shared/src/commonMain/kotlin/com/recall/app/network/NetworkClient.kt` | The shared Ktor client configuration. |
| `shared/src/commonMain/kotlin/com/recall/app/network/RecallApiService.kt` | Service class for fetching and parsing the decks. |
| `shared/src/commonTest/kotlin/com/recall/app/network/RecallApiTest.kt` | Integration tests verifying the remote fetch. |

## 🧪 Verification

To verify that the networking layer is functioning correctly, the following unit test was implemented and passed:

```kotlin
@Test
fun testFetchAllDecks() = runTest {
    val service = RecallApiService(NetworkClient.httpClient)
    val decks = service.fetchAllDecks()

    assertTrue(decks.isNotEmpty(), "Decks should not be empty")
    assertEquals(10, decks.size, "Should fetch exactly 10 decks")
    assertEquals("NASA Space Gallery", decks.first().title)
}

```

## 🚀 Running the Tests

Execute the following command to ensure the networking solution is stable on your current environment:

```bash
./gradlew shared:testDebugUnitTest

```

---

### Next Steps for the Course

This solution provides the foundation for **Module 4: Local Persistence**. Once the data is fetched via Ktor, the next challenge will be caching this data into a local SQLDelight database to enable offline study capabilities.
