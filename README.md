#### **README: Solution 5a (Adaptive Scaffold)**

# 📐 Solution 5a: The Adaptive Scaffold

**Branch Name:** `sol-lesson-5a-ui-adaptive-scaffold`

This branch implements the foundational logic for **Adaptive Layouts** in Kotlin Multiplatform. Instead of relying on Android-specific resource qualifiers (like `sw600dp`), we built a pure Compose solution that works on Android, iOS, and Desktop.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


## 🛠️ Implementation Summary

1. **Window Size Detection**: Implemented a custom `calculateWindowSize()` composable using `BoxWithConstraints`. This reads the available screen width and categorizes it into standard breakpoints.
2. **Enum Classes**: Defined `WindowSize` (Compact, Medium, Expanded) to type-safe the layout logic.
3. **Root Integration**: Updated `App.kt` to calculate the window size *once* at the top level and pass it down to all child screens.

## 📂 Key Files

| File Path | Description |
| --- | --- |
| `composeApp/.../ui/WindowSize.kt` | Contains the `WindowSize` enum and the calculation logic using `BoxWithConstraints`. |
| `composeApp/.../App.kt` | The root composable that detects the size and displays the debug text. |

## 🧪 Verification

The solution is verified if:

1. **Phone Emulator**: Displays "Current Mode: Compact".
2. **Tablet / Resizable Emulator**: When you rotate the device or resize the window past 840dp, the text automatically updates to "Expanded".

## 🚀 Next Step

The infrastructure is ready. In **Exercise 5b**, we will use this `WindowSize` to toggle between a vertical List (for phones) and a Grid (for tablets) using the "Zen Study" design.
