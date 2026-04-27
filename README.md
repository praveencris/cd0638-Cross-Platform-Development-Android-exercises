# 📱 Exercise 1: The "Hello World" of KMP

**Module:** Project Setup & Configuration
**Estimated Time:** 15-20 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**

In this first exercise, you will take a "broken" Kotlin Multiplatform project and bring it to life. Your goal is to prove that your **Shared Module** is correctly configured to run on both Android and iOS by adding a common dependency (`kotlinx-datetime`) and writing your first piece of shared code.

By the end of this lab, you will have:

1. Mastered **Version Catalogs** (`libs.versions.toml`) for dependency management.
2. Configured a **Shared Module** to use external libraries.
3. Written and tested a platform-agnostic function.

---

### **🧨 The Current State (Red)**

If you try to run the project right now, **it will fail.**

* The build system doesn't know about the libraries we need.
* The shared code has `TODO` placeholders instead of logic.
* The unit tests are failing.

**Your mission is to get everything Green.**

---

### **✅ Task 1: Fix the Version Catalog**

We need to add the `kotlinx-datetime` library to our project. In modern Android/KMP development, we define all dependencies in a single file called a **Version Catalog**.

1. **Open** the file: `gradle/libs.versions.toml`.
2. **Find** the `[libraries]` block.
3. **Action:** Add the definition for the DateTime library.
* **Group:** `org.jetbrains.kotlinx`
* **Name:** `kotlinx-datetime`
* **Version Ref:** `kotlinx-datetime`


```toml
# Hint:
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

```


4. **Sync:** Click the "Sync Now" button (Elephant icon) in the top right.

---

### **✅ Task 2: Link the Shared Module**

Defining the library isn't enough; we have to tell our **Shared Module** to actually *compile* it.

1. **Open** the file: `shared/build.gradle.kts`.
2. **Scroll** to the `sourceSets` block.
3. **Find** the `commonMain.dependencies` section.
4. **Action:** Uncomment or add the implementation line to include the library you just defined.
```kotlin
// Hint:
implementation(libs.kotlinx.datetime)

```


5. **Sync:** Sync Gradle again to download the library.

---

### **✅ Task 3: Implement Shared Logic**

Now that we have the tools, let's write the code. We want a function that returns a greeting *and* the current date, proving that our shared code works.

1. **Open** the file: `shared/src/commonMain/kotlin/com/recall/app/Platform.kt`.
2. **Locate** the `greet()` function.
3. **Action:** Replace the `TODO` with code that uses `kotlinx.datetime` to get the current date.
```kotlin
// Goal: Return a string like "Hello, Android! Today is 2026-01-26"
fun greet(): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "Hello, ${getPlatform().name}! Today is $today"
}

```


*(Note: You will need to import `kotlinx.datetime.Clock`, `kotlinx.datetime.TimeZone`, and `kotlinx.datetime.toLocalDateTime`)*

---

### **✅ Task 4: Verify Your Work**

We practice **Test-Driven Development (TDD)**. You don't need to run the full Android Simulator to check your work; just run the fast Unit Tests.

1. **Open** the **Terminal** tab in your IDE.
2. **Run** the test command:
```bash
./gradlew shared:testDebugUnitTest

```


3. **Check Results:**
* If you see **BUILD SUCCESSFUL**, you have passed Exercise 1! 🎉
* If it fails, check the error logs. Did you forget to Sync Gradle?



---

### **🆘 Troubleshooting**

* **"Unresolved reference: Clock"**: This means your `shared/build.gradle.kts` didn't link the library correctly. Check Task 2.
* **"Version not found"**: Check your spelling in `libs.versions.toml`.
* **"Method not implemented"**: Ensure you didn't accidentally delete the `getPlatform()` function in the `androidMain` or `iosMain` folders.

---

### **🚀 Bonus Challenge**

If you finish early:

* Navigate to `shared/src/androidMain/.../Platform.android.kt`.
* Change the platform name string from `"Android"` to `"Droid"`.
* Run the tests again. Does the output change?
