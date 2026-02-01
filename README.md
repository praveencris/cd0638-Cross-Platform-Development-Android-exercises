# 🪟 Exercise 6a: The Master-Detail Navigator
**Module:** Tablet & Foldable Optimization
**Estimated Time:** 20 Minutes

### **🎓 Objective**
In this exercise, you will transform the app's navigation from a simple screen switcher into a responsive **Master-Detail** system.
* On **Phones**, the app will behave like a standard stack (List screen $\rightarrow$ Detail screen).
* On **Tablets**, the app will display both screens side-by-side.

### **✅ Task 1: Manage Selection State**
We need to track *which* deck is currently active, separate from the UI.
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`.
2.  **Action**: Add a `MutableStateFlow` to hold the `selectedDeck` (nullable `Deck?`).
3.  **Action**: Expose a public function `selectDeck(deck: Deck?)` to update this state.

### **✅ Task 2: Build the Adaptive Layout**
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/navigation/MasterDetailLayout.kt`.
2.  **Action**: Create a Composable that accepts `windowSize`, `decks`, `selectedDeck`, and `onDeckSelected`.
3.  **Logic**:
    * **IF Compact (Phone):**
        * If `selectedDeck` is null $\rightarrow$ Show `LibraryScreen`.
        * If `selectedDeck` is NOT null $\rightarrow$ Show `StudyScreen` (use a placeholder for now).
        * *Hint: You'll need a `BackHandler` here to set `selectedDeck = null` when the user presses back.*
    * **IF Medium/Expanded (Tablet):**
        * Show a `Row`.
        * **Left Pane:** `LibraryScreen` (Weight 1f).
        * **Right Pane:** `StudyScreen` (Weight 2f).
        * **Divider:** Add a `VerticalDivider` between them for visual separation.

### **✅ Task 3: Wire it Up**
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/App.kt`.
2.  **Action**: Replace your old `LibraryScreen` call with your new `MasterDetailLayout`.
3.  **Action**: Connect the `MainViewModel`'s state to the layout parameters.

### **🚀 Definition of Done**
* **Phone Test:** Clicking a deck opens a "Detail" view. Pressing Back returns to the list.
* **Tablet Test:** Clicking a deck updates the Right Pane immediately without leaving the list.
