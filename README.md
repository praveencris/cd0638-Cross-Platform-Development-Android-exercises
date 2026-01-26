🧠 Exercise 2: Building the App's Brain
Module: Shared Business Logic & Models Estimated Time: 30-40 Minutes

🎓 Objective
In this exercise, you will build the "Shared Domain" of the Recall app. This is the code that is 100% shared between Android and iOS. You will define how data is structured and how the Spaced Repetition math works.

✅ Task 1: Implement Platform APIs (expect/actual)
KMP allows you to access native APIs when "Common Kotlin" isn't enough. We need to generate unique IDs for our cards.

Navigate to shared/src/androidMain/.../generateUUID.kt. Use java.util.UUID to return a string.

Navigate to shared/src/iosMain/.../generateUUID.kt. Use NSUUID().UUIDString() to return a string.

✅ Task 2: The SM-2 Algorithm
Open SpaceRepetitionAlgo.kt. You must implement the logic that powers the "Study" button seen in our design docs.

Input: A Card and a quality (0-5).

Failure (< 3): Set interval to 1.

Success (>= 3):

If interval is 0, set to 1.

If interval is 1, set to 6.

Otherwise, multiply current interval by the ease factor.

Ease: Update the ease factor using the SM-2 formula (refer to your lecture notes) and ensure it never drops below 1.3.

✅ Task 3: Verify the Logic
Run the tests:

./gradlew shared:test
Your business logic is the "Single Source of Truth." If these tests pass, your app will behave identically on both Android and iOS!
