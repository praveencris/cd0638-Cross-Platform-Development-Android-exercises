# 📊 Exercise 13a: Firebase Analytics

**Branch Name:** `lesson-13a-firebase-analytics`
**Module:** Monitoring & Analytics
**Estimated Time:** 30-40 minutes
**Part:** 1 of 2 (Crashlytics & Remote Config in Exercise 13b)

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
Learn to integrate **Firebase Analytics** in your KMP app to track user behavior across Android and iOS. You'll focus on the core challenge: implementing platform-specific type conversions while using the expect/actual pattern.

### **⚠️ iOS CocoaPods Setup (Required)**

This branch uses CocoaPods for Firebase iOS dependencies. After checking out this branch, run:

```bash
cd /path/to/Recall
./gradlew :shared:generateDummyFramework
cd iosApp && pod install
```

This generates the `Pods/` directory needed by the Xcode workspace. You only need to do this once per branch checkout. If Android Studio shows an error about `Pods/Pods.xcodeproj` not found, run the commands above.

### **⚠️ Important Notes**

**What's Pre-configured:**
The starter code includes complete build configuration:
- Firebase Analytics dependencies in `libs.versions.toml`
- CocoaPods setup with `version = "1.0"` in `shared/build.gradle.kts`
- Google Services plugin in `composeApp/build.gradle.kts`
- Example configuration files with correct JSON syntax
- `@OptIn(ExperimentalForeignApi::class)` annotations for iOS (required for compilation)

**Your Focus (30-40 minutes):**
- Implement 7 TODOs focused on Firebase Analytics
- Learn expect/actual pattern by comparing Android vs iOS implementations
- Practice platform-specific type conversions (Map ↔ Bundle, Map ↔ NSDictionary)

**What's NOT in This Exercise:**
- Crashlytics (error tracking) - covered in Exercise 13b
- Remote Config (dynamic configuration) - covered in Exercise 13b
- You'll see stub methods for these with TODO comments pointing to 13b

### **✅ Task 1: Firebase Console Setup**
1. **Go to** [console.firebase.google.com](https://console.firebase.google.com)
2. **Create Project:** Named "Recall" (or use existing project)
3. **Enable Analytics:**
   - Analytics is enabled by default for new projects
   - Go to Build → Analytics to verify

4. **Add Android:**
   - Click "Add app" → Android
   - Package name: `com.recall.app`
   - Download `google-services.json`
   - Place in `composeApp/google-services.json`
   - **Or use the example:** `cp composeApp/google-services.json.example composeApp/google-services.json` and update with your credentials

5. **Add iOS:**
   - Click "Add app" → iOS
   - Bundle ID: `com.recall.app`
   - Download `GoogleService-Info.plist`
   - Place in `iosApp/iosApp/GoogleService-Info.plist`
   - **Or use the example:** `cp iosApp/iosApp/GoogleService-Info.plist.example iosApp/iosApp/GoogleService-Info.plist` and update with your credentials

**Time: ~10 minutes**

### **✅ Task 2: Implement Android Analytics**

**Note:** Dependencies and plugins are already configured.

1. **Open** `shared/src/androidMain/kotlin/com/recall/app/analytics/AnalyticsService.android.kt`

2. **Implement 4 TODOs:**

   **TODO 1: Initialize FirebaseAnalytics**
   ```kotlin
   private val analytics: FirebaseAnalytics = // Initialize here
   ```
   - Use `FirebaseAnalytics.getInstance(context)`

   **TODO 2: Implement mapToBundle() helper**
   ```kotlin
   private fun mapToBundle(map: Map<String, Any>): Bundle {
       // Convert Kotlin types to Bundle types
       // Handle: String, Int, Long, Double, Float, Boolean
   }
   ```
   - Create a Bundle
   - Iterate through map with `.forEach { (key, value) ->`
   - Use `when (value)` to check types
   - Call appropriate `bundle.putXxx()` method

   **TODO 3: Implement logEvent()**
   ```kotlin
   override fun logEvent(name: String, params: Map<String, Any>?) {
       // Convert params using mapToBundle()
       // Call analytics.logEvent()
   }
   ```

   **TODO 4: Implement setUserProperty()**
   ```kotlin
   override fun setUserProperty(name: String, value: String) {
       // One line: analytics.setUserProperty(name, value)
   }
   ```

**Hints:**
- Use `Bundle().apply { ... }` for cleaner code
- For unknown types in mapToBundle, use `value.toString()`
- Handle null params in logEvent

**Time: ~10 minutes**

### **✅ Task 3: Implement iOS Analytics**

**Note:** CocoaPods is already configured. `@OptIn` annotation is already added.

1. **Terminal:** Run CocoaPods install:
   ```bash
   ./gradlew :shared:podInstall
   ```
   This installs Firebase Analytics pod.

2. **Open** `shared/src/iosMain/kotlin/com/recall/app/analytics/AnalyticsService.ios.kt`

3. **Implement 3 TODOs:**

   **TODO 5: Implement mapToNSDictionary() helper**
   ```kotlin
   private fun mapToNSDictionary(map: Map<String, Any>): Map<Any?, *> {
       // Convert Kotlin types to iOS types
       // Handle: String, Int, Long, Double, Float, Boolean
   }
   ```
   - Use `map.mapValues { (_, value) ->`
   - For numbers, wrap in `NSNumber`: `NSNumber(int = value)`, `NSNumber(double = value)`, etc.
   - Strings can stay as-is

   **TODO 6: Implement logEvent()**
   ```kotlin
   override fun logEvent(name: String, params: Map<String, Any>?) {
       // Convert params using mapToNSDictionary()
       // Call FIRAnalytics.logEventWithName(name, nsParams)
   }
   ```

   **TODO 7: Implement setUserProperty()**
   ```kotlin
   override fun setUserProperty(name: String, value: String) {
       // One line: FIRAnalytics.setUserPropertyString(value, name)
       // Note: iOS has reversed parameter order!
   }
   ```

**Hints:**
- The `@OptIn(ExperimentalForeignApi::class)` annotation is required and already added
- FIRAnalytics is accessed via cocoapods import
- Use `params?.let { mapToNSDictionary(it) }` to handle null

**Time: ~10 minutes**

### **✅ Task 4: Test Analytics**
1. **Open** `shared/src/commonMain/kotlin/com/recall/app/MainViewModel.kt`
2. **Find** where study sessions complete (or any user action)
3. **Add:**
   ```kotlin
   AnalyticsProvider.get().logEvent("session_complete", mapOf(
       "deck_id" to deckId,
       "card_count" to cardCount
   ))
   ```

4. **Test in Firebase DebugView:**
   - **Android:** `adb shell setprop debug.firebase.analytics.app com.recall.app`
   - **iOS:** Edit scheme → Arguments → Add `-FIRDebugEnabled`
   - Run the app and complete a study session
   - Check Firebase Console → Analytics → DebugView
   - You should see your `session_complete` event in real-time!

**Time: ~10 minutes**

### **🔧 Troubleshooting**

**iOS: "@OptIn" annotation missing**
- The annotation should already be in the starter code
- If you see compilation errors, verify lines 20 and 107 have `@OptIn(ExperimentalForeignApi::class)`

**iOS: CocoaPods installation fails**
- Ensure CocoaPods is installed: `sudo gem install cocoapods`
- The `version = "1.0"` field is already configured
- Run from project root: `./gradlew :shared:podInstall`

**Android: google-services.json not found**
- Copy from `composeApp/google-services.json.example`
- Replace placeholder values with your Firebase credentials
- Place in `composeApp/` directory (not project root)

**Events not appearing in Firebase Console**
- Use DebugView for real-time testing (see Task 4)
- Regular dashboard can take 24-48 hours
- Verify configuration files have correct project IDs

**Build: "Crashlytics" or "RemoteConfig" errors**
- These are stubs in this exercise (implemented in 13b)
- They should not cause build errors
- If you see errors, check that stub methods return default values

### **🚀 Definition of Done**
* [ ] The app compiles on both Android and iOS
* [ ] `./gradlew :shared:podInstall` completed successfully
* [ ] Example configuration files copied and updated with real credentials
* [ ] All 7 TODOs implemented (4 Android + 3 iOS)
* [ ] Analytics event appears in Firebase DebugView when you complete an action
* [ ] You understand the difference between Android Bundle and iOS NSDictionary type conversions
* [ ] Ready to continue with Exercise 13b for Crashlytics and Remote Config

### **💡 What You Learned**

After completing this exercise, you understand:
- ✅ How to use the **expect/actual pattern** for platform-specific implementations
- ✅ How to convert between **Kotlin types and platform-specific types** (Bundle, NSDictionary)
- ✅ How to integrate **Firebase Analytics** on both Android and iOS
- ✅ How to use `@OptIn` annotations for Kotlin/Native experimental APIs
- ✅ The basics of Firebase event tracking and DebugView testing

### **➡️ Next Steps**

**Continue to Exercise 13b** to add:
- **Firebase Crashlytics** - Track and analyze app crashes
- **Firebase Remote Config** - Update app behavior without releasing new versions
- **Advanced patterns** - Suspend functions with platform-specific callbacks

**Estimated time for 13b:** 30-40 minutes

### **📚 Resources**

- [Firebase Analytics Documentation](https://firebase.google.com/docs/analytics)
- [Firebase DebugView Guide](https://firebase.google.com/docs/analytics/debugview)
- [Kotlin Multiplatform expect/actual](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
- [CocoaPods Integration with KMP](https://kotlinlang.org/docs/native-cocoapods.html)
