#### **README: Solution 4b (Shared UI)**

# 🏁 Solution 4b: Shared UI & State

**Branch:** `solution-module-4b`

This branch demonstrates the power of Compose Multiplatform. We implemented the UI and State Logic **once**, and it drives both the Android and iOS applications.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

1. **Shared ViewModel**: Located in `commonMain`, this class manages the `RecallApiService` and exposes a `StateFlow<List<Deck>>`. It handles coroutines platform-agnostically.
2. **Compose Multiplatform**: The `App.kt` file defines the entire visual structure. It uses `LazyColumn` (a shared list component) to render the decks.
3. **Unified Entry Points**:
* **Android**: `MainActivity` simply sets the content to `App()`.
* **iOS**: `MainViewController` wraps `App()` in a `ComposeUIViewController` so Swift can consume it.



## 📂 Key Files

| File Path | Description |
| --- | --- |
| `composeApp/.../commonMain/.../MainViewModel.kt` | The shared state holder. |
| `composeApp/.../commonMain/.../App.kt` | The shared UI screen. |
| `composeApp/.../androidMain/.../MainActivity.kt` | Android entry point. |
| `composeApp/.../iosMain/.../MainViewController.kt` | iOS entry point. |

## 🧪 Verification

The solution is verified if:

1. **Android**: Shows the list of 10 decks.
2. **iOS**: Shows the *exact same* list of 10 decks.
