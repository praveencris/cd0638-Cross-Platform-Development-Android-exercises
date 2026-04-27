#### **README: Solution 4b (Shared UI)**

# 🏁 Solution 4b: Shared UI & State

**Branch Name:** `sol-lesson-4b-consuming-di-handshake`

This branch demonstrates the power of Compose Multiplatform. We implemented the UI and State Logic **once**, and it drives both the Android and iOS applications.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


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
