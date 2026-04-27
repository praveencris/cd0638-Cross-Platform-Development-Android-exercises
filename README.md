# 🏁 Solution 6b: Study Mode & State Hoisting
**Branch Name:** `prod-sol-lesson-6b-ui-adaptive-patterns`
This branch implements the production-grade "Study Loop," featuring 3D animations, keyboard support, and a reactive architecture for progress tracking.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

### 1. State Hoisting (The "Resume" Feature)
We moved the "Current Card Index" state out of the UI and into the `MainViewModel`.
* **Flow:** `ViewModel` holds state $\rightarrow$ Passes `startIndex` to `StudyScreen` $\rightarrow$ Screen reports `onCardReviewed` $\rightarrow$ ViewModel updates state.
* **Benefit:** This allows the user to switch decks (or rotate tablets) without losing their place.

### 2. Reactive Data Updates
To update the Progress Bar on the List Screen without refreshing the API:
```kotlin
// MainViewModel.kt
_decks.value = _decks.value.map { deck ->
    if (deck.id == targetId) deck.copy(progress = newProgress) else deck
}

```

This forces Jetpack Compose to recognize a change in the list item and redraw only that specific card's progress bar.

### 3. 3D Flip Animation

We used `graphicsLayer` for a true 3D effect.

* **Camera Distance:** Set to `12f * density` to add depth perspective.
* **Content Switching:** At 90 degrees rotation, we swap the composable from `Front` to `Back` and invert the back's rotation so text remains readable.

## 📂 Key Files

| File Path | Description |
| --- | --- |
| `composeApp/.../ui/StudyScreen.kt` | Handles Animation, Input, and reporting progress. |
| `composeApp/.../MainViewModel.kt` | Manages the `deckProgress` map and updates the Deck list. |
| `shared/.../Deck.kt` | Updated with the `progress` field. |

## 🧪 Verification

1. **Resume Test:** Open Deck A -> Go to Card 3 -> Open Deck B -> Return to Deck A. You should see Card 3.
2. **Animation Test:** Tapping the card performs a 3D flip.
3. **List Test:** Completing cards updates the green progress bar on the Library screen.
