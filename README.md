# 🧠 Exercise 6b: The Study Interface

**Branch Name:** `lesson-6b-ui-adaptive-patterns`
**Module:** Tablet & Foldable Optimization
**Estimated Time:** 45 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will build the complete "Study Loop". This includes the 3D Flip Animation, Keyboard Shortcuts, and most importantly, **State Persistence** so users can pick up exactly where they left off.

### **✅ Task 1: The "Resume" Logic**
We need the app to remember which card you were on.
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`.
2.  **Action**: Implement `getStartIndex(deckId)`. It should return the saved index from `deckProgress`.
3.  **Action**: Implement `onCardReviewed`.
    * Save the `newIndex` to the map.
    * **Crucial:** Create a *copy* of the deck in the `_decks` list with the updated `progress` (calculated as `index / total`). This triggers the UI to refresh the progress bar.

### **✅ Task 2: The Animated Card**
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/StudyScreen.kt`.
2.  **Animation**: Use `animateFloatAsState` to animate a rotation value from `0f` to `180f`.
3.  **Graphics**: Use `Modifier.graphicsLayer` to apply the rotation.
    * *Trick:* When rotation > 90 degrees, show the **Back** of the card and rotate the content 180 degrees so it isn't mirrored.

### **✅ Task 3: Connecting the Wires**
1.  **Events**: When a user clicks "Easy" or "Hard":
    * Calculate the `nextIndex`.
    * Call `onCardReviewed(nextIndex)` to notify the ViewModel.
2.  **Keyboard**: Add a `FocusRequester` and `onKeyEvent` handler.
    * **Spacebar** $\rightarrow$ Flip Card.
    * **1, 2, 3, 4** $\rightarrow$ Rate Card.

### **🚀 Definition of Done**
1.  **Visuals:** Card flips with a smooth 3D animation.
2.  **Resume:** If you leave a deck at Card #5 and come back, it starts at Card #5.
3.  **Progress:** As you review cards, the Progress Bar in the "My Library" list updates in real-time.
