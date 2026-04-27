# 🔥 Solution 13b: Crashlytics & Remote Config

**Branch Name:** `prod-final-reference`
**Module:** Monitoring & Analytics
**Part:** 2 of 2 (Building on Exercise 13a)

This solution implements the complete Firebase integration for Recall, including:
- ✅ Firebase Analytics (from Exercise 13a)
- ✅ Firebase Crashlytics for crash reporting
- ✅ Firebase Remote Config for dynamic app configuration

This provides a unified observability stack with cross-platform analytics, crash reporting, and remote configuration capabilities.

## Production Reference

This branch is the cumulative production-grade reference after Solution 13b. It keeps the course app complete while documenting the production hardening added on top of the lesson flow.

Use the matching lesson and solution branches for step-by-step study; use this branch when you want the final app-quality reference.


## ⚠️ iOS CocoaPods Setup (Required)

This branch uses CocoaPods for Firebase iOS dependencies. After checking out this branch, run:

```bash
cd /path/to/Recall
./gradlew :shared:generateDummyFramework
cd iosApp && pod install
```

This generates the `Pods/` directory needed by the Xcode workspace. You only need to do this once per branch checkout. If Android Studio shows an error about `Pods/Pods.xcodeproj` not found, run the commands above.

## 🛠️ Implementation Summary

### 1. The `expect/actual` Bridge
We created a lightweight wrapper `AnalyticsService` instead of depending on a heavy third-party KMP library.

**Why?** This keeps our shared dependency graph small and maintainable. We only bridged the 3-4 methods we actually need (`logEvent`, `setUserProperty`, `recordException`, `fetchWelcomeMessage`) rather than importing a heavyweight multiplatform SDK that brings unnecessary complexity.

**Pattern:** The `expect object AnalyticsProvider` in commonMain provides the interface, while `actual object AnalyticsProvider` in androidMain and iosMain provide platform-specific implementations.

### 2. Platform Specifics

**Android Implementation:**
- Uses the standard Firebase Android SDK via Gradle dependencies
- Leverages Firebase BOM for consistent versioning across Firebase libraries
- Converts Kotlin `Map<String, Any>` to Android `Bundle` for Firebase Analytics
- Uses `google-services` plugin to process `google-services.json` configuration

**iOS Implementation:**
- Uses native Objective-C Firebase SDKs accessed through Kotlin/Native cinterop
- Requires `@OptIn(ExperimentalForeignApi::class)` annotation due to Kotlin/Native cinterop usage
- Configured via CocoaPods in `shared/build.gradle.kts`
- Converts Kotlin data types to iOS equivalents (NSNumber, NSDictionary, NSError)
- Uses `GoogleService-Info.plist` for Firebase configuration

### 3. Remote Config
We implemented a basic `fetchWelcomeMessage()` feature as a demonstration of Firebase Remote Config capabilities.

**Use Case:** This allows us to change the text on the Home Screen (e.g., "Happy Holidays!" or "New Year Sale!") without releasing a new app update to the store. This is incredibly powerful for:
- A/B testing different messaging
- Seasonal promotions
- Emergency communications
- Gradual feature rollouts

## 📂 Key Files

| File Path | Description |
| :--- | :--- |
| `shared/src/commonMain/kotlin/com/recall/app/analytics/AnalyticsService.kt` | Shared interface and expect declaration for cross-platform analytics |
| `shared/src/androidMain/kotlin/com/recall/app/analytics/AnalyticsService.android.kt` | Android implementation using Firebase SDK |
| `shared/src/iosMain/kotlin/com/recall/app/analytics/AnalyticsService.ios.kt` | iOS implementation using Firebase via CocoaPods |
| `gradle/libs.versions.toml` | Firebase dependency versions and plugin declarations |
| `shared/build.gradle.kts` | CocoaPods configuration for iOS Firebase pods and Android dependencies |
| `composeApp/build.gradle.kts` | Google Services and Crashlytics plugin applications |
| `composeApp/google-services.json` | Android Firebase configuration (gitignored, .example provided) |
| `iosApp/iosApp/GoogleService-Info.plist` | iOS Firebase configuration (gitignored, .example provided) |

## 🚀 Setup Instructions

### Prerequisites

**1. Create a Firebase Project:**
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project" and follow the wizard
3. Enable Analytics, Crashlytics, and Remote Config in the Firebase console

**2. Download Configuration Files:**

For Android:
1. In Firebase Console, go to Project Settings → Your Apps
2. Add an Android app with package name `com.recall.app`
3. Download `google-services.json`
4. Place it in `composeApp/google-services.json`

For iOS:
1. In Firebase Console, go to Project Settings → Your Apps
2. Add an iOS app with bundle ID `com.recall.app`
3. Download `GoogleService-Info.plist`
4. Place it in `iosApp/iosApp/GoogleService-Info.plist`

**Note:** `.example` templates are provided. Copy them to the actual filenames and replace with your real Firebase credentials.

### Android Setup

**1. Verify google-services plugin is applied:**
The `composeApp/build.gradle.kts` already includes:
```kotlin
plugins {
    // ...
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}
```

**2. Initialize Analytics in your Application class:**
```kotlin
class RecallApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AnalyticsProvider.initialize(this)
    }
}
```

**3. Use the analytics service:**
```kotlin
val analytics = AnalyticsProvider.get()
analytics.logEvent("deck_created", mapOf(
    "deck_name" to "Spanish Vocabulary",
    "card_count" to 25
))
```

### iOS Setup

**1. Initialize Firebase in AppDelegate:**
Add to `iosApp/iosApp/iOSApp.swift`:
```swift
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

**2. Run CocoaPods setup:**

From the project root directory:
```bash
./gradlew :shared:podInstall
```

Or navigate to shared first:
```bash
cd shared
./gradlew podInstall
```

This generates the Podfile and installs Firebase pods. If you encounter errors, see the [Troubleshooting](#-troubleshooting) section below.

**Note:** The iOS implementation uses `@OptIn(ExperimentalForeignApi::class)` to access Firebase's Objective-C APIs through Kotlin/Native cinterop. This is already included in the solution code.

**3. Use the analytics service:**
```kotlin
val analytics = AnalyticsProvider.get()
analytics.logEvent("study_session_completed", mapOf(
    "deck_id" to deckId,
    "cards_reviewed" to cardCount,
    "duration_seconds" to duration
))
```

## 📊 Analytics Events

Common events to track in Recall:

| Event Name | Parameters | Purpose |
| :--- | :--- | :--- |
| `deck_created` | `deck_name`, `card_count` | Track deck creation activity |
| `study_session_started` | `deck_id`, `card_count` | Monitor study engagement |
| `study_session_completed` | `deck_id`, `cards_reviewed`, `duration_seconds` | Measure completion rates |
| `card_flipped` | `deck_id`, `card_id` | Track user interactions |
| `card_marked_known` | `deck_id`, `card_id` | Understand learning progress |

## 🐛 Crash Reporting

**Recording Non-Fatal Exceptions:**
```kotlin
try {
    riskyOperation()
} catch (e: Exception) {
    AnalyticsProvider.get().recordException(e)
    // Handle gracefully
}
```

**Setting User Context:**
```kotlin
AnalyticsProvider.get().setUserProperty("deck_count", deckCount.toString())
AnalyticsProvider.get().setUserProperty("user_tier", "premium")
```

## 🔧 Remote Config Example

**Fetching Configuration:**
```kotlin
suspend fun getWelcomeMessage(): String {
    return AnalyticsProvider.get().fetchWelcomeMessage()
}
```

**In Firebase Console:**
1. Go to Remote Config
2. Add parameter `welcome_message`
3. Set value to "Welcome to Recall!" (default)
4. Create conditions for A/B testing or targeting
5. Publish changes

Your app will fetch the latest value on next launch!

## 🎯 Learning Objectives

After completing this module, you understand:

- **expect/actual Pattern:** How to create clean abstractions for platform-specific code in KMP
- **Dependency Management:** Using Firebase BOM for Android and CocoaPods for iOS
- **Type Conversion:** Mapping Kotlin types to platform-specific types (Bundle, NSDictionary, NSError)
- **Analytics Best Practices:** What events to track and why
- **Crash Reporting:** How to capture and analyze non-fatal exceptions
- **Remote Configuration:** Changing app behavior without app updates
- **Lightweight Wrappers:** Why minimal bridges are better than heavyweight multiplatform SDKs

## 🔍 Verification

**Test Analytics:**
1. Run the app on Android or iOS
2. Trigger an analytics event
3. Check Firebase Console → Analytics → Events (may take a few hours to appear)

**Test Crashlytics:**
1. Record a test exception
2. Force-close the app
3. Reopen the app to send crash reports
4. Check Firebase Console → Crashlytics

**Test Remote Config:**
1. Change `welcome_message` value in Firebase Console
2. Call `fetchWelcomeMessage()` in your app
3. Verify the new message is retrieved

## 💡 Best Practices

1. **Initialize Early:** Set up Analytics in Application.onCreate() (Android) or AppDelegate.init() (iOS)
2. **Meaningful Event Names:** Use descriptive, consistent naming (snake_case recommended)
3. **Parameter Limits:** Firebase has limits on event parameters (25 per event, 100 chars per string)
4. **User Privacy:** Always comply with GDPR/CCPA - provide opt-out mechanisms
5. **Test Thoroughly:** Use Firebase Test Lab for comprehensive testing
6. **Monitor Budgets:** Analytics and Crashlytics have free tier limits

## 🔧 Troubleshooting

### iOS Issues

**Compilation Error: ExperimentalForeignApi**
```
Error: This declaration needs opt-in. Its usage must be marked with '@OptIn(ExperimentalForeignApi::class)'
```
**Solution:** The iOS implementation requires `@OptIn(ExperimentalForeignApi::class)` annotation for Kotlin/Native cinterop. This is already included in the solution code (`AnalyticsService.ios.kt`).

**CocoaPods Installation Fails**
```
Error: pod install failed
```
**Solutions:**
1. Ensure CocoaPods is installed: `sudo gem install cocoapods`
2. Run from project root: `./gradlew :shared:podInstall`
3. If Podfile.lock conflicts exist, delete `shared/build/cocoapods/` and retry
4. Verify Xcode command line tools: `xcode-select --install`

**Framework Not Found in Xcode**
- Ensure you opened the `.xcworkspace` file, not `.xcodeproj`
- Run `./gradlew :shared:podInstall` again
- Clean build folder in Xcode (Cmd+Shift+K)

### Android Issues

**AnalyticsProvider Not Initialized**
```
IllegalStateException: AnalyticsProvider not initialized. Call initialize(context) first.
```
**Solution:** Call `AnalyticsProvider.initialize(this)` in your Application class's `onCreate()` method before accessing analytics.

**google-services.json Not Found**
```
Error: File google-services.json is missing
```
**Solution:**
1. Download from Firebase Console → Project Settings → Your Apps → Download google-services.json
2. Place in `composeApp/google-services.json` (not in project root)
3. Verify package name in JSON matches `"com.recall.app"`
4. Ensure file is not in `.gitignore` by mistake

**Release Build Crashes with Firebase**

If analytics work in debug but crash in release builds, add ProGuard rules to `composeApp/proguard-rules.pro`:

```proguard
# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
```

### Common Issues

**Events Not Appearing in Firebase Console**
- Events can take 24-48 hours to appear in the Firebase dashboard
- Use **DebugView** for real-time testing (enable debug mode in your app)
- Verify configuration files have correct project IDs matching your Firebase project

**iOS Simulator Warnings**
Warnings like `Failed to send CA Event` or `nw_socket_set_connection_idle` are harmless iOS system warnings. They don't affect Firebase functionality and can be safely ignored.

## 📚 Additional Resources

- [Firebase Analytics Documentation](https://firebase.google.com/docs/analytics)
- [Firebase Crashlytics Documentation](https://firebase.google.com/docs/crashlytics)
- [Firebase Remote Config Documentation](https://firebase.google.com/docs/remote-config)
- [Kotlin Multiplatform expect/actual](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
- [CocoaPods Integration with KMP](https://kotlinlang.org/docs/native-cocoapods.html)
