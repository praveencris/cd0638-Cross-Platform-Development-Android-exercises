# 🏁 Solution 8b: Offline Intelligence
**Branch Name:** `sol-lesson-8b-db-foundation-setup`

This branch completes the Offline-First implementation. The app is now fully robust against network flakiness.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


## 🛠️ Implementation Summary

### 1. Database Instantiation (KMP)
Creating a Room database requires platform-specific context and configuration.

**Android Setup:**
* Uses `DatabaseFactory(context)` to pass the Application Context
* SQLiteDriver is automatically provided by Room
* Database file location: `context.getDatabasePath("recall.db")`

**iOS Setup:**
* Uses `NSHomeDirectory()` to find a valid file path for `recall.db`
* Requires explicit `BundledSQLiteDriver()` configuration via `.setDriver()`
* Requires `@ConstructedBy` annotation on database class for Room KMP code generation
* Must declare `expect object RecallDatabaseConstructor` for platform-specific instantiation

**Koin DI:**
* Platform-specific modules (`androidModule`, `iosModule`) provide `DatabaseFactory`
* Shared `sharedModule` creates `RecallDatabase` singleton from factory
* Only one database connection exists across the entire app

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
| `DatabaseFactory.kt` | Platform-agnostic expect/actual pattern for DB instantiation. |
| `DatabaseFactory.android.kt` | Android-specific database creation with Context. |
| `DatabaseFactory.ios.kt` | iOS-specific database creation with NSHomeDirectory and BundledSQLiteDriver. |
| `AndroidModule.kt` | Android DI module providing DatabaseFactory with context. |
| `IosModule.kt` | iOS DI module providing DatabaseFactory (no context). |
| `Koin.kt` | Shared dependency injection wiring (sharedModule). |
| `RecallDatabase.kt` | Database schema definition and migration (v1 → v2). |
| `RecallDao.kt` | Data access methods including `updateDeckProgress()`. |
| `Mappers.kt` | Bidirectional conversion between entities and domain models. |
| `MainViewModel.kt` | Database-backed progress tracking and resume logic. |

## 🧪 Verification
1.  **Offline Test:** Turn on Airplane mode and launch the app. It works.
2.  **Sync Test:** Add a deck on the server (mock), pull-to-refresh, and see it appear.
3.  **Progress Test:** Study 4 cards in a deck, close the app, reopen it, and verify you resume at card 5.
4.  **Progress Preservation:** Pull-to-refresh after studying and verify your progress is maintained.
