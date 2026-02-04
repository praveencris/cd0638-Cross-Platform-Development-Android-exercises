# 🏁 Solution 2: Building the App's Brain

**Branch:** `solution-module-2`

This branch contains the core **Domain Layer** of the Recall app. This layer is platform-independent and contains the data structures and algorithms that serve as the foundation for the entire project.

## 🛠️ Implementation Summary

### 1. Serializable Domain Models

We implemented the `Deck` and `Card` models using `kotlinx-serialization`.

* **API Alignment:** We used `@SerialName` to ensure our Kotlin camelCase properties correctly map to the snake_case JSON returned by the Gist API (e.g., `image_base64` maps to `imageBase64`).
* **Logic Integration:** We included fields like `interval` and `ease` directly in the `Card` model. These fields allow the SM-2 algorithm to persist the state of each card across study sessions.

### 2. The `expect`/`actual` Bridge

Because Kotlin `commonMain` does not have access to platform-specific APIs like Java's `java.util.UUID` or iOS's `NSUUID`, we implemented a bridge:

* **Expectation:** Defined `generateUUID()` in `commonMain`.
* **Android Actual:** Utilized `java.util.UUID`.
* **iOS Actual:** Utilized `NSUUID` from the iOS Foundation framework.
* **Result:** We can now generate unique identifiers for new cards created in the shared logic while maintaining native performance on each device.

### 3. SM-2 Spaced Repetition Algorithm

The `SpaceRepetitionAlgo` is the mathematical core of the app.

* **Quality Scores:** We mapped four UI actions (Again, Hard, Good, Easy) to a 0–5 scale.
* **Interval Calculation:** We implemented logic to reset intervals for forgotten cards (Quality < 3) and grow them exponentially for remembered cards (Quality >= 3).
* **Ease Factor:** We implemented the standard SM-2 formula for updating the "Easiness" of a card, ensuring it never drops below **1.3** to avoid "Ease Hell" (where a card repeats too frequently).

## 📂 Key Files

| File Path | Description |
| --- | --- |
| `shared/.../domain/models/` | The `Card` and `Deck` data structures. |
| `shared/.../Platform.kt` | The `expect` declaration for UUID generation. |
| `shared/.../domain/SpaceRepetitionAlgo.kt` | The mathematical logic for spaced repetition. |

## 🧪 Verification Results

The domain logic was verified using **Shared Unit Tests** that run on the JVM.

* **Success:** A "Good" review on a new card correctly results in a **6-day** interval.
* **Success:** An "Again" review correctly resets the interval to **1 day**, regardless of its previous progress.
* **Success:** The ease factor is correctly capped at a minimum of **1.3**.

## 💡 Key Takeaway

By keeping the "Brain" in the shared module, we ensure that the study experience is identical on Android and iOS. If we decide to tweak the SM-2 algorithm in the future, we only have to change the code in one place.

**Would you like me to move on to the implementation plan for the Data Layer (Module 3)?**
