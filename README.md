# 🏁 Solution 6a: Master-Detail Architecture
**Branch Name:** `sol-lesson-6a-ui-adaptive-patterns`

This branch implements a "Production Grade" Master-Detail layout that handles the nuances of container sizing versus device sizing.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


## 🛠️ Implementation Summary

### 1. The "Portrait = Single Pane" Rule
We intentionally grouped `WindowSize.Medium` (Tablets in Portrait) with `Compact`.
* **Why?** A split view on a 600-800dp screen results in two unusable narrow columns. By keeping Portrait tablets in "Single Pane" mode, the user gets a rich, full-screen Grid experience.

### 2. The "Container Override" Pattern
In `MasterDetailLayout.kt`, we applied a crucial fix for the Split View:

```kotlin
// Inside Split View (Landscape)
Box(Modifier.weight(1f)) {
    LibraryScreen(
        windowSize = WindowSize.Compact, // <--- THE KEY FIX
        decks = decks,
        ...
    )
}

```

* **The Problem:** Passing `WindowSize.Expanded` to the side pane caused the `LibraryScreen` to attempt rendering a 2-column Grid inside a 400dp space, resulting in squashed text.
* **The Solution:** By passing `WindowSize.Compact`, we instruct the component to render its "List Variant," which is optimized for narrow spaces. This respects the **Container Size** rather than the **Device Size**.

### 3. State-Driven Navigation

Navigation is decoupled from the UI framework (no `NavController` arguments).

* **Stack Mode:** Uses `BackHandler` to nullify the `selectedDeck` state.
* **Split Mode:** Selection simply triggers a recomposition of the right pane.

## 📂 Key Files

| File Path | Description |
| --- | --- |
| `composeApp/.../ui/navigation/MasterDetailLayout.kt` | Contains the Adaptive Logic and the "Container Override" fix. |
| `composeApp/.../MainViewModel.kt` | Manages the navigation state (`selectedDeck`). |

## 🧪 Verification

The solution is verified if:

1. **Tablet Landscape:** Shows a clean List (Left) and Detail (Right).
2. **Tablet Portrait:** Shows a full-screen Grid (Single Pane).
3. **Phone:** Shows a standard navigation stack.
