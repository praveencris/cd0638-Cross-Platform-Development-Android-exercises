# 🌍 Exercise 9: Going Global (Internationalization)

**Branch Name:** `lesson-9-internationalization`
**Module:** Internationalization & Localization
**Prerequisite:** Completed Module 8c (Performance & Polish)
**Estimated Time:** 45 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will transform Recall from an English-only app into a global product. You will extract all hardcoded strings into **Resource Files**, implement **Spanish** support, and handle complex grammar rules using **Plurals**.

### **✅ Task 1: Extracting Strings**
Currently, our UI text is "hardcoded" in Kotlin files. We need to move it to a central XML repository so it can be translated.
1.  **Open** `composeApp/src/commonMain/composeResources/values/strings.xml`.
2.  **Action**: Define string resources for the following:
    * `library_title`: "My Library"
    * `study_button`: "Study"
    * `due_count_label`: "%d due"
3.  **Refactor UI**:
    * Open `LibraryScreen.kt` and replace `"My Library"` with `stringResource(Res.string.library_title)`.

### **✅ Task 2: Handling Plurals**
"1 cards" is a grammar error. We need to use plural resources to handle singular vs. plural forms correctly.
1.  **Open** `strings.xml`.
2.  **Action**: Add a `<plurals>` resource:
    ```xml
    <plurals name="cards_count">
        <item quantity="one">%d card</item>
        <item quantity="other">%d cards</item>
    </plurals>
    ```
3.  **Refactor UI**:
    * Open `DeckCard.kt` and replace the manual string formatting with `pluralStringResource(Res.plurals.cards_count, count, count)`.

### **✅ Task 3: Localizing for Spanish (Hola!)**
1.  **Open** `composeApp/src/commonMain/composeResources/values-es/strings.xml`.
2.  **Action**: Translate all keys from Task 1 and 2 into Spanish.
    * *Examples:* "Mi Biblioteca", "Estudiar", "%d cartas".
3.  **Test**:
    * Change your Android Emulator's system language to **Spanish**.
    * **Verify**: The app UI should automatically switch to Spanish without a recompile.

### **🚀 Definition of Done**
* [ ] No hardcoded English strings remain in `LibraryScreen.kt` or `DeckCard.kt`.
* [ ] "1 card" displays correctly in singular form, and "2 cards" in plural.
* [ ] Switching the device language to Spanish updates the app UI instantly.
