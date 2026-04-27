### **Exercise 4a: The DI Handshake (README)**

# 🤝 Exercise 4a: Linking & Dependency Injection

**Module:** Consuming KMP in Android

**Estimated Time:** 20 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**

In this exercise, you will establish the physical and logical connection between the Android App and the Shared KMP module. You will configure the build system to recognize the shared code and set up **Koin** to manage dependencies across the platform boundary.

### **🛠️ Prerequisites**

* Completion of Exercise 3 (Networking).
* The `:shared` module must be compiling successfully.

---

### **✅ Task 1: Link the Modules**

The Android application (`composeApp`) currently has no idea the `:shared` module exists.

1. **Open** `composeApp/build.gradle.kts`.
2. **Action**: In the `dependencies` block, add the project reference:
```kotlin
implementation(project(":shared"))

```


3. **Action**: Add the `koin-android` library to your dependencies.
4. **Sync Gradle**.

---

### **✅ Task 2: Configure Shared DI**

We need a way to initialize Koin that works for both Android and (eventually) iOS.

1. **Open** `shared/src/commonMain/kotlin/com/recall/app/di/Koin.kt`.
2. **Action**: Complete the `initKoin` function. It should accept an `appDeclaration` to allow the Android app to pass its `androidContext`.
3. **Action**: Ensure the `RecallApiService` is registered as a `single` (singleton) in the `sharedModule`.

---

### **✅ Task 3: The Android Handshake**

Now, tell the Android app to start the engine when it boots up.

1. **Open** `composeApp/src/androidMain/kotlin/com/recall/app/RecallApp.kt`.
2. **Action**: In `onCreate()`, call the `initKoin` function you just finished in Task 2.
3. **Action**: Pass the `androidContext(this@RecallApp)` to the initialization block so Koin knows how to handle Android-specific resources.

---

### **🚀 Definition of Done**

* The `composeApp` module compiles without "Unresolved reference" errors for shared classes.
* The application launches on an emulator/device without a Koin initialization crash.
