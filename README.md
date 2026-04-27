# 🏁 Solution 7c: Wear OS Data Polish
**Branch:** `sol-lesson-7c-wear-polish`
**Prerequisite:** Completed Exercise 7b

This branch replaces the hardcoded mock data in the Tile and Complication with real data fetched from `RecallApiService`. The Tile now displays the actual first deck name and total card count, and the Complication shows the real number of cards across all decks.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

### 1. Real Data in TileService
We replaced the hardcoded `deckName = "Physics"` and `dueCount = 5` with a live fetch from `RecallApiService`.
* **Pattern:** Access Koin from an Android Service using `KoinPlatform.getKoin().get<RecallApiService>()`
* **Coroutine Strategy:** Use `runBlocking` — this is acceptable because TileService callbacks run on a background thread managed by the Tiles framework
* **Error Handling:** If the API call fails, we fall back to `("Recall", 0)` so the Tile still renders

### 2. Real Data in ComplicationService
We replaced the hardcoded `dueCount = 12` with a live fetch using the same pattern.
* **Pattern:** Same `KoinPlatform.getKoin()` + `runBlocking` approach
* **Preview Data:** `getPreviewData()` intentionally still returns a static "12" — preview data is shown in the watch face editor before any real data is available
* **Error Handling:** Falls back to `0` on API failure

### Why KoinPlatform.getKoin() Instead of koinInject()?
`TileService` and `ComplicationDataSourceService` are **Android Services**, not Compose components. They do not participate in Compose's composition lifecycle, so `koinInject()` (which is a `@Composable` function) cannot be used. Instead, we access Koin's global container directly via `KoinPlatform.getKoin().get<T>()`.

### Why runBlocking is Acceptable Here
Both `TileService.onTileRequest()` and `ComplicationDataSourceService.onComplicationRequest()` are called on **background threads** managed by the respective Wear OS frameworks. Using `runBlocking` on these threads does NOT block the main/UI thread. This is different from Activities or Compose, where `runBlocking` would cause ANRs.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `wear/.../tile/RecallTileService.kt` | Tile now fetches real deck data from API |
| `wear/.../complication/RecallComplicationService.kt` | Complication now fetches real card count from API |

## 🎓 Learning Objectives
* Accessing dependency injection (Koin) from Android Services using `KoinPlatform.getKoin()`
* Using coroutine patterns (`runBlocking`) in non-Compose contexts where background threads are guaranteed
* Implementing graceful error handling with fallback values for glanceable surfaces

## 🧪 Verification
1. **Tile:** Install on a Wear OS emulator. Swipe to the tile carousel — the tile should show the actual first deck name (e.g., "Kotlin Basics") and total card count instead of "Physics: 5 Due"
2. **Complication:** Add the Recall complication to a watch face slot — it should show the real total card count instead of always showing "12"
3. **Error Case:** Disable network on the emulator — the tile should show "Recall: 0 Due" and the complication should show "0" (graceful fallback)

## 🔧 Troubleshooting
* **Koin not initialized:** If you see `KoinApplication has not been started`, ensure `WearApplication` is set as the application class in `AndroidManifest.xml` and that `initKoin` is called in `onCreate()`
* **Tile not updating:** Tiles cache aggressively. Uninstall and reinstall the app, or use `adb shell am broadcast -a com.google.android.clockwork.home.action.REQUEST_TILE_UPDATE` to force a refresh
* **Network timeout:** The GitHub Gist URL may be slow on first call. The `runBlocking` call will wait, which is fine on the background thread but may cause the tile to take a moment to appear
