# 🏁 Solution 8b: Offline Intelligence
**Branch:** `solution-module-8b`

This branch completes the Offline-First implementation. The app is now fully robust against network flakiness.

## 🛠️ Implementation Summary

### 1. Database Instantiation (KMP)
Creating a Room database requires platform-specific context.
* **Android:** Used `DatabaseFactory(context)` to pass the Application Context.
* **iOS:** Used `NSHomeDirectory()` to find a valid file path for `recall.db`.
* **Koin:** We registered `RecallDatabase` as a `Single` in our DI graph, ensuring only one database connection exists.

### 2. Single Source of Truth (SSOT)
The `RecallRepository` no longer returns API results directly to the UI.
* **Flow:** `DAO -> Repository -> ViewModel -> UI`.
* **Sync:** `ViewModel -> Repository.refresh() -> API -> DAO`.
* **Benefit:** The UI never sees a "Loading" spinner for cached data. It shows the old data immediately, then silently updates when the new data arrives.

### 3. Data Mapping
We implemented mappers to convert between layers:
* `DeckEntity.toDomain()`: Used when reading from DB.
* `Deck.toEntity()`: Used when writing API results to DB.
* *Why?* This decouples our database schema from our business logic.

### 4. Persistent Progress Tracking
Study progress now persists across app restarts using database storage.
* **Database Schema:** Added `currentCardIndex` column to `DeckEntity` (v1 → v2 migration).
* **Migration:** Uses `ALTER TABLE` to safely add column to existing databases (defaults to 0).
* **Resume Functionality:** `getStartIndex()` reads progress from database, `onCardReviewed()` persists updates.
* **Network Sync:** `refreshDecks()` preserves existing progress when syncing from API (prevents reset on pull-to-refresh).
* **Reactive Updates:** Database Flow automatically updates UI when progress changes.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `RecallRepository.kt` | The brain of the Offline-First logic. |
| `DatabaseFactory.kt` | Platform-specific code to create the DB file. |
| `Koin.kt` | Dependency Injection wiring. |
| `RecallDatabase.kt` | Database schema definition and migration (v1 → v2). |
| `RecallDao.kt` | Data access methods including `updateDeckProgress()`. |
| `Mappers.kt` | Bidirectional conversion between entities and domain models. |
| `MainViewModel.kt` | Database-backed progress tracking and resume logic. |

## 🧪 Verification
1.  **Offline Test:** Turn on Airplane mode and launch the app. It works.
2.  **Sync Test:** Add a deck on the server (mock), pull-to-refresh, and see it appear.
3.  **Progress Test:** Study 4 cards in a deck, close the app, reopen it, and verify you resume at card 5.
4.  **Progress Preservation:** Pull-to-refresh after studying and verify your progress is maintained.
