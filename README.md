# ⌚ Exercise 7c: Wear OS Data Polish
**Module:** Advanced Wear OS Integration (continued)
**Prerequisite:** Completed Exercise 7b
**Estimated Time:** 30 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In Exercise 7b, you implemented a Tile and Complication using hardcoded mock data (`"Physics"`, `5`, `12`). In this exercise, you will replace that mock data with **real data** fetched from `RecallApiService`.

### **📖 Background: Accessing Koin from Android Services**
`TileService` and `ComplicationDataSourceService` are **Android Services** — they are NOT Compose components. This means you **cannot** use `koinInject()` (which is a `@Composable` function).

Instead, you access Koin's global container directly:
```kotlin
val apiService = KoinPlatform.getKoin().get<RecallApiService>()
```

Since both Services receive their callbacks on **background threads** (managed by the Tiles/Complications frameworks), you can safely use `runBlocking` to call suspend functions without blocking the UI thread.

### **✅ Task 1: Replace Mock Data in RecallTileService**
1. **Open** `wear/src/main/java/com/recall/wear/tile/RecallTileService.kt`
2. **Find** the TODO 1 comment block in `onTileRequest()`
3. **Replace** the hardcoded `deckName` and `dueCount` with:
   - Get `RecallApiService` from Koin
   - Call `fetchAllDecks()` inside `runBlocking { }`
   - Extract: first deck title + total card count across all decks
   - Handle errors with fallback values `("Recall", 0)`
4. **Add imports** at the top of the file:
   - `import kotlinx.coroutines.runBlocking`
   - `import org.koin.mp.KoinPlatform`
   - `import com.recall.app.network.RecallApiService`

### **✅ Task 2: Replace Mock Data in RecallComplicationService**
1. **Open** `wear/src/main/java/com/recall/wear/complication/RecallComplicationService.kt`
2. **Find** the TODO 2 comment block in `onComplicationRequest()`
3. **Replace** the hardcoded `dueCount = 12` with:
   - Same Koin + runBlocking pattern as Task 1
   - Calculate: `decks.sumOf { it.cards.size }`
   - Handle errors with fallback value `0`
4. **Do NOT modify** `getPreviewData()` — preview data should remain static

### **🚀 Definition of Done**
* [ ] **Tile:** Shows actual deck name from API (e.g., "Kotlin Basics") instead of "Physics"
* [ ] **Tile:** Shows real total card count instead of hardcoded "5"
* [ ] **Complication:** Shows real total card count instead of hardcoded "12"
* [ ] **Error Handling:** With network disabled, tile shows "Recall: 0 Due" and complication shows "0"
* [ ] **Preview:** Complication preview in watch face editor still shows "12" (unchanged)

### **💡 Hints**
* `KoinPlatform` is in the `org.koin.mp` package
* Kotlin destructuring: `val (name, count) = Pair("a", 1)` extracts both values
* `decks.sumOf { it.cards.size }` gives you the total card count across all decks
* `decks.first().title` gives you the name of the first deck
