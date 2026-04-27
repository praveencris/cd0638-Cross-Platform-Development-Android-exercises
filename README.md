# ⌚ Exercise 7a: Wear OS Fundamentals
**Module:** Wear OS Development
**Estimated Time:** 30 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will create a standalone **Wear OS** app for Recall. Unlike the mobile app, this interface is optimized for **5-second interactions** on a round screen. You will learn to use `ScalingLazyColumn` and the "Fish-Eye" effect.

### **🚫 The "Glanceable" Rule**
Wear OS apps are not for browsing.
* **DO:** Show a simple list of decks with their "Due" counts.
* **DON'T:** Try to cram the complex Study Mode or Charts onto the wrist yet.

### **✅ Task 1: Explore the New Module**
We have added a new module `:wear` to the project.
1.  **Open** `settings.gradle.kts` and verify that `include(":wear")` is present.
2.  **Open** `wear/build.gradle.kts`. Notice the dependencies:
    * `androidx.wear.compose`: The specialized UI kit for watches.
    * `com.google.android.horologist`: Google's library for advanced Wear layouts.
    * `project(":shared")`: We are reusing our existing Data & Logic layers!

### **✅ Task 2: The Fish-Eye List**
On a round screen, a standard `LazyColumn` clips content at the corners. We must use a specialized list.
1.  **Open** `wear/src/main/java/com/recall/wear/presentation/WearAppContent.kt`.
2.  **Locate** the `WearAppContent` composable.
3.  **Action**: Replace the placeholder `Text` with a `ScalingLazyColumn`.
    * *Hint:* Use `Horologist`'s `AppScaffold` and `ScreenScaffold` if available, or the standard `Scaffold` with `TimeText` to show the clock at the top.

### **✅ Task 3: The Chip Component**
Buttons on watches need to be big (stadium-shaped) and easy to tap.
1.  **Action**: Inside your list, iterate through `viewModel.decks`.
2.  **Action**: For each deck, render a `Chip` (from `androidx.wear.compose.material`).
    * **Label**: The Deck Title.
    * **Secondary Label**: "${deck.cards.size} cards".
    * **Icon**: Use the deck's category icon.
    * **Colors**: Use `ChipDefaults.primaryChipColors()` or customized gradients.

### **✅ Task 4: Run on Emulator**
1.  **Select** the `wear` configuration in your IDE run dropdown.
2.  **Device**: Create or select a "Wear OS Large Round" emulator (API 30+).
3.  **Verify**:
    * The app launches with a black background (OLED optimization).
    * As you scroll, items shrink and fade at the top/bottom edges (the "Scaling" effect).

### **🚀 Definition of Done**
* [ ] The app runs on a Wear OS emulator.
* [ ] You see a scrollable list of decks.
* [ ] The list implements the "Fish-Eye" scaling effect.
* [ ] The UI uses large, tap-friendly `Chip` components.
