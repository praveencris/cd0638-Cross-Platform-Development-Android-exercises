# 🎨 Exercise 5b: The Responsive Dashboard

**Branch Name:** `lesson-5b-ui-responsive-dashboard`
**Module:** Building Adaptive UIs
**Estimated Time:** 20-30 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**

In this exercise, you will implement the "Zen Study" design aesthetic. You will build a high-fidelity `DeckCard` component and use the `WindowSize` logic from Exercise 5a to toggle between a **List Layout** (for phones) and a **Grid Layout** (for tablets).

### **🎨 Design Reference**

* **Style:** Minimalist, Whitespace-heavy, Rounded Corners, Soft Gradients (Blue/Orange/Green).
* **Color Palette:** Blue (`#3B82F6` → `#1E40AF`), Orange (`#F97316` → `#C2410C`), Emerald (`#10B981` → `#047857`). Use gradient pairs for card backgrounds.
* Refer to the `DeckCard.kt` function signature and docstring for the expected component structure.

---

### **✅ Task 1: Build the Zen Card**

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/components/DeckCard.kt`.
2. **Action**: Implement the UI to match the Zen Study aesthetic described above.
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
* **Colors look wrong?**: Refer to the hex color codes listed in the Design Reference section above.

---

### **🚀 Definition of Done**

* The UI matches the "Zen Study" minimalist design.
* The app adapts layout structure based on the device size.
* The code passes a self-audit for best practices (no hardcoded magic numbers, proper modifier usage).
