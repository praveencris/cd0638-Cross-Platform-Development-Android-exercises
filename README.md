# 🔥 Exercise 13b: Crashlytics & Remote Config
**Module:** Monitoring & Analytics
**Estimated Time:** 30-40 minutes
**Part:** 2 of 2 (Building on Exercise 13a)

### **🎓 Objective**
Build on your working Firebase Analytics from Exercise 13a to add **Crashlytics** (crash reporting) and **Remote Config** (dynamic app configuration). You'll learn advanced KMP patterns for error handling and async operations.

### **⚠️ iOS CocoaPods Setup (Required)**

This branch uses CocoaPods for Firebase iOS dependencies. After checking out this branch, run:

```bash
cd /path/to/Recall
./gradlew :shared:generateDummyFramework
cd iosApp && pod install
```

This generates the `Pods/` directory needed by the Xcode workspace. You only need to do this once per branch checkout. If Android Studio shows an error about `Pods/Pods.xcodeproj` not found, run the commands above.

### **⚠️ Important Notes**

**Prerequisites:**
- ✅ **You MUST complete Exercise 13a first** - Analytics should be working
- ✅ Events appearing in Firebase DebugView
- ✅ All 7 TODOs from 13a implemented

**What's Pre-configured:**
Everything from Exercise 13a, PLUS:
- Firebase Crashlytics and Remote Config dependencies in `libs.versions.toml`
- Crashlytics plugin in `composeApp/build.gradle.kts`
- All CocoaPods already installed (FirebaseCrashlytics, FirebaseRemoteConfig)
- `@OptIn(ExperimentalForeignApi::class)` annotations for iOS
- **Your working Analytics implementation from 13a**

**Your Focus (30-40 minutes):**
- Implement 7 TODOs focused on Crashlytics and Remote Config
- Learn to convert Kotlin exceptions to platform-specific error types
- Master suspend functions with platform-specific async callbacks
- Understand dynamic app configuration with Remote Config

**What You Already Have:**
- ✅ Working Firebase Analytics from Exercise 13a
- ✅ Type conversion patterns (mapToBundle, mapToNSDictionary)
- ✅ expect/actual pattern understanding
- ✅ Firebase configuration files in place

### **✅ Task 1: Firebase Console Setup**

Since you completed Exercise 13a, your Firebase project already exists. Now enable the new services:

1. **Go to** [console.firebase.google.com](https://console.firebase.google.com)
2. **Open your Recall project** (created in 13a)

3. **Enable Crashlytics:**
   - Go to Build → Crashlytics
   - Click "Enable Crashlytics"
   - Follow any setup prompts
   - No additional configuration files needed

4. **Enable Remote Config:**
   - Go to Build → Remote Config
   - Click "Get started"
   - Add parameter: `welcome_message`
   - Set default value: `"Welcome to Recall!"`
   - Publish changes

5. **Verify CocoaPods (Optional):**
   ```bash
   ./gradlew :shared:podInstall
   ```
   Should show UP-TO-DATE if 13a setup was complete.

**Time: ~5 minutes**

### **✅ Task 2: Implement Android Crashlytics & Remote Config**

**Note:** Analytics is already working from Exercise 13a. Focus only on the new features.

1. **Open** `shared/src/androidMain/kotlin/com/recall/app/analytics/AnalyticsService.android.kt`

2. **Implement 3 TODOs:**

   **TODO 1: Initialize Crashlytics and Remote Config in constructor**
   ```kotlin
   private val crashlytics: FirebaseCrashlytics = // Initialize here
   private val remoteConfig: FirebaseRemoteConfig = // Initialize here
   ```
   - Use `FirebaseCrashlytics.getInstance()`
   - Use `FirebaseRemoteConfig.getInstance()`
   - Set Remote Config fetch interval: `remoteConfig.setConfigSettingsAsync()`
   - Set default values: `remoteConfig.setDefaultsAsync(mapOf("welcome_message" to "Welcome to Recall!"))`

   **TODO 2: Implement recordException()**
   ```kotlin
   override fun recordException(throwable: Throwable) {
       // One line: crashlytics.recordException(throwable)
   }
   ```

   **TODO 3: Implement fetchWelcomeMessage() suspend function**
   ```kotlin
   override suspend fun fetchWelcomeMessage(): String = suspendCancellableCoroutine { continuation ->
       // Use suspendCancellableCoroutine to wrap Firebase's async callback
       // 1. Call remoteConfig.fetchAndActivate()
       // 2. Add OnCompleteListener
       // 3. If successful, get value: remoteConfig.getString("welcome_message")
       // 4. On failure, return default: "Welcome to Recall!"
       // 5. Resume continuation with result
   }
   ```

**Hints:**
- For Remote Config settings, use `FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()`
- Use `.addOnCompleteListener { task ->` for async callback
- Call `continuation.resume(result)` to return from suspend function
- Handle failures gracefully with default values

**Time: ~10-15 minutes**

### **✅ Task 3: Implement iOS Crashlytics & Remote Config**

**Note:** Analytics is already working. The `@OptIn` annotation is already added.

1. **Open** `shared/src/iosMain/kotlin/com/recall/app/analytics/AnalyticsService.ios.kt`

2. **Implement 4 TODOs:**

   **TODO 4: Initialize FIRRemoteConfig with settings in constructor**
   ```kotlin
   private val remoteConfig: FIRRemoteConfig = // Initialize here
   ```
   - Get instance: `FIRRemoteConfig.remoteConfig()`
   - Create settings: `FIRRemoteConfigSettings()`
   - Set minimum fetch interval: `settings.setMinimumFetchInterval(3600.0)`
   - Apply settings: `remoteConfig.setConfigSettings(settings)`
   - Set defaults: `remoteConfig.setDefaults(mapOf("welcome_message" to "Welcome to Recall!"))`

   **TODO 5: Implement recordException()**
   ```kotlin
   override fun recordException(throwable: Throwable) {
       // 1. Convert throwable to NSError using toNSError() extension
       // 2. Call FIRCrashlytics.crashlytics().recordError(error)
   }
   ```

   **TODO 6: Implement Throwable.toNSError() extension**
   ```kotlin
   private fun Throwable.toNSError(): NSError {
       // Create NSError using NSError.errorWithDomain()
       // - domain: "com.recall.app"
       // - code: 0
       // - userInfo: mapOf with "message" and "stackTrace" keys
       // Use this.message ?: "Unknown error" and this.stackTraceToString()
   }
   ```

   **TODO 7: Implement fetchWelcomeMessage() suspend function**
   ```kotlin
   override suspend fun fetchWelcomeMessage(): String = suspendCancellableCoroutine { continuation ->
       // Use suspendCancellableCoroutine to wrap iOS async callback
       // 1. Call remoteConfig.fetchAndActivateWithCompletionHandler { status, error -> }
       // 2. Check if error == null
       // 3. Get value: remoteConfig.configValueForKey("welcome_message").stringValue
       // 4. Use ?? "Welcome to Recall!" for fallback
       // 5. Call continuation.resume(message)
   }
   ```

**Hints:**
- `NSError.errorWithDomain()` takes domain, code, and userInfo parameters
- For userInfo, pass `mapOf<Any?, Any?>("message" to ..., "stackTrace" to ...)`
- The completion handler provides `status` and `error` - check `error == null`
- Use `?.stringValue ?: "default"` for safe unwrapping in Kotlin

**Time: ~15-20 minutes**

### **✅ Task 4: Test Crashlytics & Remote Config**

1. **Test Crashlytics:**
   - Add a button in your UI that throws a test exception:
   ```kotlin
   Button(onClick = {
       AnalyticsProvider.get().recordException(
           RuntimeException("Test crash from Exercise 13b")
       )
   }) { Text("Test Crashlytics") }
   ```
   - Run the app and click the button
   - Go to Firebase Console → Crashlytics
   - You should see the exception logged (may take a few minutes)

2. **Test Remote Config:**
   - Add code to fetch and display the welcome message:
   ```kotlin
   LaunchedEffect(Unit) {
       val message = AnalyticsProvider.get().fetchWelcomeMessage()
       println("Welcome message: $message")
   }
   ```
   - Run the app and check logs for "Welcome to Recall!"
   - Go to Firebase Console → Remote Config
   - Change `welcome_message` to something different (e.g., "Welcome back!")
   - Publish changes
   - Force close and restart the app
   - Should fetch and display the new message

**Time: ~5-10 minutes**

### **🔧 Troubleshooting**

**iOS: "ExperimentalForeignApi" errors**
- The `@OptIn(ExperimentalForeignApi::class)` annotation should already be present
- Required for FIRCrashlytics and FIRRemoteConfig cinterop
- Verify it's on both the class and the actual object

**iOS: toNSError() type inference errors**
- Use explicit types for userInfo: `mapOf<Any?, Any?>("message" to ...)`
- iOS APIs expect `Map<Any?, *>` for NSDictionary compatibility

**Android: suspendCancellableCoroutine errors**
- Import: `import kotlinx.coroutines.suspendCancellableCoroutine`
- Import: `import kotlin.coroutines.resume`
- Make sure you're calling `continuation.resume()` in the listener

**Crashlytics: Events not appearing**
- Non-fatal exceptions can take 5-10 minutes to appear in console
- Use "Test crash" button in Crashlytics dashboard for immediate verification
- Ensure Crashlytics is enabled in Firebase Console

**Remote Config: Not fetching new values**
- Default fetch interval is 12 hours in production
- Use 3600 seconds (1 hour) for testing as configured
- Call `fetchAndActivate()` not just `fetch()` - activate is required

**CocoaPods: Pods not found**
- If you get "pod not found" errors, run: `./gradlew :shared:podInstall`
- Should show UP-TO-DATE if 13a was completed correctly
- If not, verify `version = "1.0"` is in `shared/build.gradle.kts` cocoapods block

### **🚀 Definition of Done**
* [ ] All 7 TODOs from Exercise 13b implemented (3 Android + 4 iOS)
* [ ] The app compiles on both Android and iOS
* [ ] Test exception appears in Firebase Crashlytics dashboard
* [ ] Welcome message fetches from Remote Config
* [ ] Changed welcome message in console reflects in app after restart
* [ ] You understand suspend functions with platform-specific async callbacks
* [ ] You understand converting Kotlin exceptions to platform error types

### **💡 What You Learned**

After completing this exercise, you understand:
- ✅ How to implement **Firebase Crashlytics** for crash reporting on both platforms
- ✅ How to implement **Firebase Remote Config** for dynamic app configuration
- ✅ How to **convert Kotlin exceptions to platform-specific error types** (Throwable → NSError)
- ✅ How to **wrap platform async callbacks in suspend functions** using suspendCancellableCoroutine
- ✅ Advanced expect/actual patterns with async operations
- ✅ Complete Firebase integration: Analytics, Crashlytics, and Remote Config

### **🎯 Key Advanced Pattern: Suspend Functions with Native Callbacks**

This exercise taught a critical KMP pattern used throughout multiplatform development:

**Android:**
```kotlin
suspend fun fetchData(): String = suspendCancellableCoroutine { continuation ->
    firebaseApi.fetch()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                continuation.resume(task.result)
            } else {
                continuation.resume("default")
            }
        }
}
```

**iOS:**
```kotlin
suspend fun fetchData(): String = suspendCancellableCoroutine { continuation ->
    firebaseApi.fetchWithCompletionHandler { result, error ->
        val value = if (error == null) {
            result ?: "default"
        } else {
            "default"
        }
        continuation.resume(value)
    }
}
```

This pattern bridges native async APIs to Kotlin coroutines, enabling clean, cross-platform suspend functions.

### **🏆 Module 13 Complete!**

Congratulations! You've completed the Monitoring & Analytics module by implementing:
- **Exercise 13a:** Firebase Analytics with type conversions
- **Exercise 13b:** Crashlytics and Remote Config with advanced async patterns

You now have a production-ready monitoring setup that tracks user behavior, reports crashes, and supports dynamic configuration updates.

### **📚 Resources**

- [Firebase Crashlytics Documentation](https://firebase.google.com/docs/crashlytics)
- [Firebase Remote Config Documentation](https://firebase.google.com/docs/remote-config)
- [Kotlin Coroutines suspendCancellableCoroutine](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/suspend-cancellable-coroutine.html)
- [Kotlin/Native cinterop](https://kotlinlang.org/docs/native-c-interop.html)
- [NSError in Kotlin/Native](https://kotlinlang.org/api/latest/jvm/stdlib/platform.foundation/-n-s-error/)
