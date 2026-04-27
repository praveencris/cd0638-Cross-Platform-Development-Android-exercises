# 📊 Exercise 13a: Firebase Analytics - Solution
**Module:** Monitoring & Analytics
**Part:** 1 of 2 (Crashlytics & Remote Config in Exercise 13b)

## iOS CocoaPods Setup (Required)

This branch uses CocoaPods for Firebase iOS dependencies. After checking out this branch, run:

```bash
cd /path/to/Recall
./gradlew :shared:generateDummyFramework
cd iosApp && pod install
```

This generates the `Pods/` directory needed by the Xcode workspace. You only need to do this once per branch checkout. If Android Studio shows an error about `Pods/Pods.xcodeproj` not found, run the commands above.

## Solution Overview

This branch contains the complete solution for Exercise 13a, demonstrating how to integrate Firebase Analytics in a Kotlin Multiplatform app with proper type conversions for both Android and iOS platforms.

### What's Implemented

✅ **Android Analytics** (`shared/src/androidMain/kotlin/com/recall/app/analytics/AnalyticsService.android.kt`):
- FirebaseAnalytics initialization
- `logEvent()` - Event tracking with parameter conversion
- `setUserProperty()` - User property tracking
- `mapToBundle()` helper - Kotlin Map → Android Bundle conversion

✅ **iOS Analytics** (`shared/src/iosMain/kotlin/com/recall/app/analytics/AnalyticsService.ios.kt`):
- FIRAnalytics integration via CocoaPods
- `logEvent()` - Event tracking with parameter conversion
- `setUserProperty()` - User property tracking
- `mapToNSDictionary()` helper - Kotlin Map → iOS NSDictionary conversion

✅ **Cross-platform Architecture**:
- expect/actual pattern for platform-specific implementations
- Shared interface in commonMain
- Type-safe analytics API

### What's NOT Implemented (Coming in Exercise 13b)

⏳ **Firebase Crashlytics** - Crash reporting
⏳ **Firebase Remote Config** - Dynamic app configuration
⏳ Advanced suspend functions with native callbacks
⏳ Throwable to NSError conversion

These features are stubbed out and return default values.

## Key Learning Points

### 1. Platform-Specific Type Conversion

**Android - Map to Bundle:**
```kotlin
private fun mapToBundle(map: Map<String, Any>): Bundle {
    val bundle = Bundle()
    map.forEach { (key, value) ->
        when (value) {
            is String -> bundle.putString(key, value)
            is Int -> bundle.putInt(key, value)
            is Long -> bundle.putLong(key, value)
            is Double -> bundle.putDouble(key, value)
            is Float -> bundle.putFloat(key, value)
            is Boolean -> bundle.putBoolean(key, value)
            else -> bundle.putString(key, value.toString())
        }
    }
    return bundle
}
```

**iOS - Map to NSDictionary:**
```kotlin
private fun mapToNSDictionary(map: Map<String, Any>): Map<Any?, *> {
    return map.mapValues { (_, value) ->
        when (value) {
            is String -> value
            is Int -> NSNumber(int = value)
            is Long -> NSNumber(longLong = value)
            is Double -> NSNumber(double = value)
            is Float -> NSNumber(float = value)
            is Boolean -> NSNumber(bool = value)
            else -> value.toString()
        }
    }
}
```

### 2. expect/actual Pattern

**Common Interface:**
```kotlin
// commonMain
expect object AnalyticsProvider {
    fun get(): AnalyticsService
}
```

**Android Implementation:**
```kotlin
// androidMain
actual object AnalyticsProvider {
    private lateinit var instance: AnalyticsService

    fun initialize(context: Context) {
        instance = AndroidAnalyticsService(context.applicationContext)
    }

    actual fun get(): AnalyticsService = instance
}
```

**iOS Implementation:**
```kotlin
// iosMain
@OptIn(ExperimentalForeignApi::class)
actual object AnalyticsProvider {
    private val instance: AnalyticsService by lazy {
        IOSAnalyticsService()
    }

    actual fun get(): AnalyticsService = instance
}
```

### 3. iOS Specific Requirements

**@OptIn Annotation Required:**
```kotlin
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    // Implementation
}
```

This annotation is required for Kotlin/Native cinterop to access iOS Firebase APIs.

**Firebase Initialization in Swift:**

Unlike Android (where the google-services Gradle plugin auto-initializes Firebase), iOS requires explicit initialization in `iOSApp.swift`:

```swift
import FirebaseCore

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()
    }
    // ...
}
```

This is possible because `iosApp/Podfile` declares Firebase pods directly, giving Swift access to `FirebaseCore`.

**CocoaPods Integration:**

The `shared/build.gradle.kts` includes a static framework configuration and Firebase pods:
```kotlin
cocoapods {
    version = "1.0"
    framework {
        baseName = "shared"
        isStatic = true  // Required for Firebase static xcframeworks
    }
    pod("FirebaseAnalytics") {
        version = "~> 11.8.0"
        extraOpts += listOf("-compiler-option", "-fmodules")
    }
}
```

The `iosApp/Podfile` declares Firebase pods separately for Swift access:
```ruby
platform :ios, '15.0'
target 'iosApp' do
  use_frameworks! :linkage => :static
  pod 'FirebaseCore', '~> 11.8.0'
  pod 'FirebaseAnalytics', '~> 11.8.0'
  pod 'FirebaseCrashlytics', '~> 11.8.0'
  pod 'FirebaseRemoteConfig', '~> 11.8.0'
end
```

> **Important:** The Podfile does NOT reference `pod 'shared'` because `ComposeApp.framework` already includes the shared module's Kotlin code via Gradle. Including both would cause a "runtime injected twice" crash.

## Testing the Solution

### Android

1. **Enable DebugView:**
   ```bash
   adb shell setprop debug.firebase.analytics.app com.recall.app
   ```

2. **Run the app** and complete a study session

3. **Check Firebase Console** → Analytics → DebugView
   - You should see events in real-time

### iOS

1. **Setup CocoaPods** (first time only):
   ```bash
   ./gradlew :shared:generateDummyFramework
   cd iosApp && pod install
   ```

2. **Enable DebugView:**
   - Edit scheme → Arguments → Add `-FIRDebugEnabled`

3. **Build and run** from Xcode using `iosApp.xcworkspace` (not `.xcodeproj`), or from Android Studio

4. **Check Firebase Console** → Analytics → DebugView
   - You should see `"Firebase initialized on iOS"` in console
   - Events appear in real-time in DebugView

## Build Configuration

All Firebase dependencies and plugins are pre-configured:

**Versions** (`gradle/libs.versions.toml`):
- Firebase BOM: 33.7.0 (explicit versions used for KMP compatibility)
- iOS Firebase pods: ~> 11.8.0

**Android** (`composeApp/build.gradle.kts`):
- Google Services plugin applied
- Firebase Crashlytics plugin applied (for Exercise 13b)

**iOS** (`shared/build.gradle.kts`):
- CocoaPods configuration with static framework and Firebase pods
- `@OptIn` compiler flags configured

**iOS App** (`iosApp/Podfile`):
- Firebase pods for Swift-side initialization
- Static linkage to match Firebase xcframeworks

## Troubleshooting

**iOS: ExperimentalForeignApi errors**
- The `@OptIn(ExperimentalForeignApi::class)` annotation is required and included
- Applied to both IOSAnalyticsService class and AnalyticsProvider object

**Android: AnalyticsProvider not initialized**
- Ensure you call `AnalyticsProvider.initialize(context)` in Application.onCreate()

**Events not appearing**
- Use DebugView for real-time testing (see above)
- Regular dashboard can take 24-48 hours

**iOS: CocoaPods errors**
- Run: `./gradlew :shared:generateDummyFramework` then `cd iosApp && pod install`
- The `version = "1.0"` field is required and included
- Build from `iosApp.xcworkspace` (not `.xcodeproj`)

**iOS: Bundle ID mismatch warning**
- Ensure `iosApp/Configuration/Config.xcconfig` has `PRODUCT_BUNDLE_IDENTIFIER=com.recall.app`
- This must match the bundle ID in `GoogleService-Info.plist`

**iOS: "runtime injected twice" crash**
- Do NOT add `pod 'shared'` to `iosApp/Podfile` — the shared module is already included via `ComposeApp.framework`

## Next Steps

Continue to **Exercise 13b** to add:
- Firebase Crashlytics for crash reporting
- Firebase Remote Config for dynamic configuration
- Advanced patterns: suspend functions with native callbacks
- Throwable to NSError conversion for iOS

**Estimated time for 13b:** 30-40 minutes

## Resources

- [Firebase Analytics Documentation](https://firebase.google.com/docs/analytics)
- [Kotlin Multiplatform expect/actual](https://kotlinlang.org/docs/multiplatform-expect-actual.html)
- [CocoaPods Integration with KMP](https://kotlinlang.org/docs/native-cocoapods.html)

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.

