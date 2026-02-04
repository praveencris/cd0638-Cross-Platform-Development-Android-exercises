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

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `RecallRepository.kt` | The brain of the Offline-First logic. |
| `DatabaseFactory.kt` | Platform-specific code to create the DB file. |
| `Koin.kt` | Dependency Injection wiring. |

## 🧪 Verification
1.  **Offline Test:** Turn on Airplane mode and launch the app. It works.
2.  **Sync Test:** Add a deck on the server (mock), pull-to-refresh, and see it appear.
