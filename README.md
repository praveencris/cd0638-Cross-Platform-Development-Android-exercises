# 🪟 Exercise 6a: The Master-Detail Navigator
**Module:** Tablet & Foldable Optimization
**Estimated Time:** 25 Minutes

### **🎓 Objective**
In this exercise, you will transform the app's navigation into a responsive **Master-Detail** system.
* On **Phones & Portrait Tablets**, the app behaves like a standard stack.
* On **Landscape Tablets & Desktops**, the app evolves into a side-by-side dashboard.

### **✅ Task 1: Manage Selection State**
We need to track *which* deck is currently active, separate from the UI.
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`.
2.  **Action**: Add a `MutableStateFlow` to hold the `selectedDeck` (nullable `Deck?`).
3.  **Action**: Expose a public function `selectDeck(deck: Deck?)` to update this state.

### **✅ Task 2: The Adaptive Logic**
We need to decide when to split the screen.
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/navigation/MasterDetailLayout.kt`.
2.  **Action**: Create the `MasterDetailLayout` composable.
3.  **Logic**:
    * **IF Expanded (> 840dp):** Show **Split View**.
    * **ELSE (Compact/Medium):** Show **Stack View**.
    * *Note: We treat "Medium" (Portrait Tablet) as a single screen to give the content more room.*

### **✅ Task 3: Implement the Views**
Implement the two branches of your logic:

**Branch A: The Stack (Phones/Portrait)**
* If `selectedDeck` is NOT null $\rightarrow$ Show `StudyScreen`.
    * *Crucial:* Wrap this in a `BackHandler` that sets `selectedDeck = null` when pressed.
* If `selectedDeck` IS null $\rightarrow$ Show `LibraryScreen`.
    * Pass the actual `windowSize` to the library so it can choose between List vs. Grid.

**Branch B: The Split (Landscape Tablets)**
* Create a `Row`.
* **Left Pane (Weight 1):** Show `LibraryScreen`.
    * **⚠️ Important:** Explicitly pass `WindowSize.Compact` to the library here.
    * *Why?* Even though the device is "Expanded", this specific pane is narrow (~400dp). Forcing "Compact" tells the library to render a clean List instead of a squashed Grid.
* **Vertical Divider**: Standard visual separator.
* **Right Pane (Weight 2):** Show `StudyScreen` (if a deck is selected) or a Placeholder (if null).

### **✅ Task 4: Integration**
1.  **Open** `composeApp/src/commonMain/kotlin/com/recall/app/App.kt`.
2.  **Action**: Replace the old `LibraryScreen` call with `MasterDetailLayout`.
3.  **Action**: Connect the `MainViewModel` state.

### **🚀 Definition of Done**
* **Phone:** Navigates in and out of decks like a standard app.
* **Tablet (Portrait):** Shows a full-screen Grid (Single Pane).
* **Tablet (Landscape):** Shows the List on the left and Detail on the right.
* **Visual Check:** The List in the split-view is NOT squashed; it looks like a clean vertical list.
