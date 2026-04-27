# 🏁 Solution 8a: Database Foundation
**Branch Name:** `sol-lesson-8a-db-foundation-setup`
This branch implements the persistence layer using **Room KMP**. It enables the shared business logic to store data locally on both Android and iOS devices.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


## 🛠️ Implementation Summary

### 1. Schema Definition (Entities)
We created two tables that mirror our Domain models:
* **`DeckEntity`**: Simple parent table.
* **`CardEntity`**: Child table with a **Foreign Key** to `DeckEntity` (`ON DELETE CASCADE`). This ensures data integrity—orphaned cards are automatically removed.

### 2. Data Access (DAO)
We implemented `RecallDao` with:
* **Reactive Queries:** `getDecks()` returns a `Flow`. This is crucial for "Offline-First" apps. When the DB changes (e.g., a background sync), the UI updates automatically.
* **Suspend Functions:** One-shot operations (Insert/Delete) use Kotlin Coroutines.
* **The "Trap":** We intentionally added `getCardsForDeck` (a synchronous-style lookup) to demonstrate the **N+1 Query Problem** in the upcoming Profiling module (8c).

### 3. Room Configuration (The Heavy Lifting)
Room KMP requires careful setup in `libs.versions.toml` and `build.gradle.kts`:
* **KSP (Kotlin Symbol Processing):** Used to generate the SQLite code implementation from our annotations.
* **SQLite Bundled:** We included `androidx.sqlite:sqlite-bundled` so the app ships with a consistent SQLite version, rather than relying on the specific OS version of the user's phone.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `shared/.../local/entity/` | `DeckEntity` and `CardEntity` definitions. |
| `shared/.../local/dao/RecallDao.kt` | Interface defining SQL interactions. |
| `shared/.../local/RecallDatabase.kt` | The main database entry point. |
| `shared/build.gradle.kts` | KSP and Schema directory configuration. |

## 🧪 Verification
1.  **Build:** Run `./gradlew :shared:assembleDebug`.
2.  **Success:** If the build passes, Room has successfully generated the implementation code (look in `shared/build/generated/ksp/`).
