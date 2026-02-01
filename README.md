# 🎨 Exercise 5b: The Responsive Dashboard

**Branch:** `lesson-5b-ui-responsive-dashboard`
**Module:** Building Adaptive UIs
**Estimated Time:** 20-30 Minutes

### **🎓 Objective**

In this exercise, you will implement the "Zen Study" design aesthetic. You will build a high-fidelity `DeckCard` component and use the `WindowSize` logic from Exercise 5a to toggle between a **List Layout** (for phones) and a **Grid Layout** (for tablets).

### **🎨 Design Reference**

* **Location:** `root/plan/design/main_list_zen_study_-_minimalist_variant/`
* **Key Asset:** `screen.png` (Visual Reference) & `code.html` (Color/Tailwind Specs)
* **Style:** Minimalist, Whitespace-heavy, Rounded Corners, Soft Gradients (Blue/Orange/Green).

---

### **✅ Task 1: Build the Zen Card**

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/components/DeckCard.kt`.
2. **Action**: Implement the UI to match the design screenshot.
* **Container**: Rounded corners (`24.dp`), Gradient background.
* **Content**: Title (Bold), Subtitle (Opacity 60%), "Due" Badge (Pill shape), Progress Bar.
* **Colors**: Dynamically apply the hex color from the `Deck` model.


3. **Audit**: Ensure your code is production-grade. Use `Arrangement.spacedBy` for padding and `MaterialTheme` typography.

### **✅ Task 2: The Adaptive Layout**

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/LibraryScreen.kt`.
2. **Action**: Consume the `WindowSize` parameter passed down from `App.kt`.
3. **Logic**:
* **IF Compact**: Render a `LazyColumn` (Vertical List).
* **IF Medium/Expanded**: Render a `LazyVerticalGrid` with 2 columns.


4. **Refactor**: Pass your `decks` data into this layout and render a `DeckCard` for each item.

### **✅ Task 3: Visual Verification**

1. **Phone Test**: Launch on an Android Phone or iPhone Simulator. You should see a vertical list of beautiful cards.
2. **Tablet Test**: Launch on a Tablet or resize the window. The layout should snap into a 2-column grid.

---

### **🆘 Troubleshooting**

* **"Unresolved Reference: dp"**: Make sure you import `androidx.compose.ui.unit.dp`.
* **Grid not showing?**: Ensure you are using `LazyVerticalGrid` with `GridCells.Fixed(2)`.
* **Colors look wrong?**: Check `code.html` in the design folder for the exact Hex codes used in the mockups.

---

### **🚀 Definition of Done**

* The UI matches the "Zen Study" minimalist design.
* The app adapts layout structure based on the device size.
* The code passes a self-audit for best practices (no hardcoded magic numbers, proper modifier usage).
