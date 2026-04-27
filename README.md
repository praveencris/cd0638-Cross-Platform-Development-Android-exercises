# ⚡ Exercise 8c: Performance & Polish

**Branch Name:** `lesson-8c-performance-optimization`
**Module:** Persistence & Performance
**Prerequisite:** Completed Module 8b
**Estimated Time:** 40 Minutes (80 with Bonus)

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
The app works, but is it *fast*? In this exercise, you will use the **Android Profiler** to find two serious bugs: a database bottleneck and a memory leak.
* **Required:** Fix the N+1 Query and the Memory Leak.
* **Optional Bonus:** Implement a professional "Shimmer" loading state.

---

### **🕵️ Part 1: The Performance Hunt (Required)**

#### **Case 1: The "N+1" Query**
1.  **Profile:** Open **App Inspection > Database Inspector**. Notice that loading the list triggers 1 query for Decks + 1 query *per* Deck for cards.
2.  **Fix:**
    * Create `DeckWithCards` entity (`@Embedded deck`, `@Relation cards`).
    * Update `RecallDao` to use `@Transaction` and fetch everything in one query.
    * Update `RecallRepository` to use this new efficient method.

#### **Case 2: The Memory Leak**
1.  **Profile:** Run the **Memory Profiler**. Rotate the screen 5 times. Capture a **Heap Dump**.
2.  **Analyze:** Find `MainActivity`. You will see multiple instances!
3.  **Fix:** Remove the static `companion object` reference in `MainActivity.kt`.

---

### **✨ Part 2: The UI Polish (Optional Bonus)**
*Make your app feel professional by replacing the loading spinner with a skeleton screen.*

#### **Task A: The Skeleton Card**
We provided a helper `Shimmer.kt` that contains the animation logic.
1.  **Open** `ui/components/DeckCardPlaceholder.kt`.
2.  **Build the UI:** Create a Composable that mimics the layout of `DeckCard` (Row, Column, Boxes), but:
    * Replace `Text` and `Icons` with simple `Box` shapes.
    * Apply the `.shimmerEffect()` modifier to these boxes.
    * *Tip:* Match the `height` (~200dp) and `padding` of the real card exactly to prevent layout shifts.

#### **Task B: Smart Loading**
1.  **Open** `LibraryScreen.kt`.
2.  **Logic:** Instead of just showing the list, check the state:
    ```kotlin
    if (decks.isEmpty() && isRefreshing) {
        // Show 6 DeckCardPlaceholders
    } else {
        // Show the real list
    }
    ```
3.  **Test:** Uninstall the app (to clear DB) and launch it. You should see the pulsing skeleton UI before the data loads!

---

### **🚀 Definition of Done**
* [ ] **Profiler:** Database Inspector shows only 1 query for the list.
* [ ] **Profiler:** Memory Heap Dump shows only 1 `MainActivity`.
* [ ] **(Bonus)** App launch shows a "Shimmer" effect instead of a blank screen.
