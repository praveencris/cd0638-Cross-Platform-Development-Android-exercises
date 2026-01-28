### **Exercise 5a: The Adaptive Scaffold (README)**

# 📐 Exercise 5a: The Adaptive Scaffold

**Module:** Building Adaptive UIs
**Estimated Time:** 20 Minutes

### **🎓 Objective**

In this exercise, you will lay the foundation for a responsive application. You will implement a pure Kotlin Multiplatform mechanism to detect the screen size of the device (Phone vs. Tablet) without relying on Android XML resource qualifiers. This "Window Size Class" logic will drive the layout decisions for the entire app.

### **🛠️ Prerequisites**

* Completion of Module 4b (Shared UI).
* A project structure where `commonMain` is actively driving the UI.

---

### **✅ Task 1: Define Window Sizes**

We need a type-safe way to categorize devices.

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/ui/WindowSize.kt`.
2. **Action**: Define an `enum class WindowSize` with three entries:
* `Compact` (Phones)
* `Medium` (Foldables/Small Tablets)
* `Expanded` (Tablets/Desktop)



### **✅ Task 2: Implement Detection Logic**

Now, write the Composable that actually measures the screen.

1. **Action**: In the same file, implement the `calculateWindowSize()` function.
2. **Logic**: Use `BoxWithConstraints` to access the `maxWidth`.
* **< 600.dp**  Return `Compact`
* **< 840.dp**  Return `Medium`
* **> 840.dp**  Return `Expanded`


3. **Audit**: Ensure you strictly follow the Material Design 3 breakpoint guidelines.

### **✅ Task 3: Wire Up the Root**

1. **Open** `composeApp/src/commonMain/kotlin/com/recall/app/App.kt`.
2. **Action**: Call `calculateWindowSize()` at the very top of your `App` composable.
3. **Action**: For now, replace your UI with a temporary text label: `"Window Mode: $windowSize"`.
4. **Test**: Run the app on a Phone Emulator and a Tablet Emulator (or resize the desktop preview). Ensure the text updates correctly.

---

### **🚀 Definition of Done**

* The `WindowSize` enum is defined in `commonMain`.
* The app compiles and runs on both Android and iOS.
* The UI displays "Compact" on phones and "Expanded" on tablets.
