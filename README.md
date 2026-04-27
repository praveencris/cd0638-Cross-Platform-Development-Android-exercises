# 🧠 Exercise 2: Building the App's Brain

**Module:** Shared Business Logic & Data Models
**Estimated Time:** 30–40 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**

In this exercise, you will build the "Shared Domain" of the Recall app. This layer contains the core data structures and the mathematical "Brain" (the SM-2 algorithm) that powers the study sessions. This code is written once in the `shared` module and is utilized by both the Android and iOS applications.

By the end of this lab, you will have:

1. Reviewed the **Data Models** that match our Gist API.
2. Implemented **Expect/Actual** declarations for platform-specific UUID generation.
3. Built the **SM-2 Spaced Repetition Algorithm**.
4. Verified the logic using **Unit Tests**.

---

### **🛠️ Prerequisites**

* Completion of Exercise 1 (Project Setup).
* The project should be in a state where `commonMain` is ready for new classes.

---

### **✅ Task 1: Implement Shared Data Models**

Our app needs to handle Decks and Cards. These models must align with the JSON structure provided by our Gist API while including the fields needed for the study algorithm.

1. **Review** the existing models in `com.recall.app.domain.models`.
2. **Understand** the `Card` and `Deck` data classes and how they map to the JSON structure from the Gist API.
* **Fields for Card:** `id`, `front`, `back`, `imageBase64`, `interval` (Int), and `ease` (Float).
* **Fields for Deck:** `id`, `title`, `colorHex`, and `cards` (List of Cards).
* **Note:** Serialization annotations (`@Serializable`, `@SerialName`) will be added in Lesson 3 when we introduce Ktor networking.



---

### **✅ Task 2: Bridge the Platforms (`expect`/`actual`)**

KMP allows us to define a function in `commonMain` and provide the implementation in platform-specific folders. We need this to generate unique IDs (UUIDs).

1. **Define the Expectation:** In `commonMain`, create `expect fun generateUUID(): String`.
2. **Provide the Android Actual:** In `androidMain`, implement it using `java.util.UUID.randomUUID().toString()`.
3. **Provide the iOS Actual:** In `iosMain`, implement it using `NSUUID().UUIDString()`.

---

### **✅ Task 3: The SM-2 Algorithm**

This is the logic that determines when a user sees a card again based on how well they remembered it.

1. **Open** `SpaceRepetitionAlgo.kt` in `commonMain`.
2. **Map the UI Buttons:** Our design features 4 buttons. Map them to these quality scores:
* **Again** = 0
* **Hard** = 2
* **Good** = 4
* **Easy** = 5


3. **Implement the Calculation:**
* **If Quality < 3:** The user forgot. Reset `interval` to **1**.
* **If Quality >= 3:** * If `interval` is 0  set to 1.
* If `interval` is 1  set to 6.
* Otherwise  `round(interval * ease)`.


* **Ease Update:** Update the `ease` factor using the standard SM-2 formula. **Important:** Ensure the ease factor never drops below **1.3**.



---

### **✅ Task 4: Verify Your Work**

The domain logic is the "Single Source of Truth." Before we ever build a UI, we ensure the math is perfect.

1. **Open** the **Terminal** tab.
2. **Run** the tests:
```bash
./gradlew shared:test

```


3. **Verification:** * Check that a "Good" (4) review on a new card results in a 6-day interval.
* Check that an "Again" (0) review resets any interval back to 1.



---

### **🆘 Troubleshooting**

* **"Unresolved reference: @Serializable"**: Serialization annotations are not needed for this lesson — they will be introduced in Lesson 3.
* **"Actual declaration missing"**: Double-check that your package names and function signatures match exactly between the `commonMain`, `androidMain`, and `iosMain` files.
* **Math precision**: When calculating the new ease, remember to convert your result to a `Float` and use `.coerceAtLeast(1.3f)`.

---

### **🚀 Definition of Done**

* Data models match the Gist JSON structure.
* UUID generation works on both Android and iOS targets.
* Unit tests pass with 100% success for the SM-2 logic.
