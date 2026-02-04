# 🏁 Solution 9: Internationalization
**Branch:** `solution-module-9`

This branch refactors the application to support multi-language environments using the **Compose Multiplatform Resources** library.

## 🛠️ Implementation Summary

### 1. Resource Architecture
We transitioned from hardcoded strings to a structured resource system:
* **`commonMain/composeResources/values/`**: Contains the base `strings.xml` (English).
* **`commonMain/composeResources/values-es/`**: Contains the Spanish localization.
* **Mechanism**: The app now uses `stringResource()` and `pluralStringResource()` which dynamically resolve the correct text based on the user's system locale.

### 2. Advanced Pluralization
We implemented the `<plurals>` resource type to handle grammatical variations in card counts.
* **Benefit**: This ensures that "1 card" and "12 cards" use the correct singular/plural suffixes, providing a polished, professional user experience.

### 3. Smart Fallbacks
The system is configured with English as the default. If a user sets their device to a language we don't support (e.g., French), the app will gracefully fall back to the English resources in the base `values/` directory.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `composeApp/.../values/strings.xml` | Primary English resource file. |
| `composeApp/.../values-es/strings.xml` | Spanish translation resource file. |
| `ui/LibraryScreen.kt` | Refactored to use dynamic string resolution. |
| `ui/components/DeckCard.kt` | Refactored to use the pluralization engine. |

## 🧪 Verification
1.  **Locale Switching**: Change the device language in System Settings. The app title and labels should update immediately.
2.  **Plural Check**: Verify that a deck with a single card displays "1 card" and a deck with multiple cards displays "X cards".
