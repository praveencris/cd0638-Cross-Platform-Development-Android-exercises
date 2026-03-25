# Firebase Analytics Integration — Step-by-Step Guide

## What This Lesson Teaches

This solution demonstrates how to integrate **Firebase Analytics** into a Kotlin Multiplatform app using the `expect/actual` pattern. Instead of relying on a heavyweight multiplatform Firebase library, we build a lightweight wrapper that bridges only the methods we need — keeping the dependency graph small and maintainable.

## Prerequisites

Before starting, ensure:
- A Firebase project exists at [Firebase Console](https://console.firebase.google.com/)
- Module 12a/12b (Fastlane) is complete
- `gradle.properties` has version properties (single source of truth)

> **Firebase Account Required:** You need a Google account and a Firebase project. Firebase Analytics is free for standard usage. No paid plan required.

---

## Step 1: Create a Firebase Project

### Using Firebase Console

1. Go to [console.firebase.google.com](https://console.firebase.google.com/)
2. Click **Add project**
3. Enter project name: `Recall`
4. Enable Google Analytics (recommended)
5. Select or create a Google Analytics account
6. Click **Create project**

### Add Android App

1. In Firebase Console, click **Add app** > **Android**
2. Package name: `com.recall.app` (must match `applicationId` in `build.gradle.kts`)
3. App nickname: `Recall Android`
4. Click **Register app**
5. Download `google-services.json`
6. Place it in `composeApp/google-services.json`

### Add iOS App

1. In Firebase Console, click **Add app** > **iOS**
2. Bundle ID: `com.recall.app` (must match `PRODUCT_BUNDLE_IDENTIFIER` in `Config.xcconfig`)
3. App nickname: `Recall iOS`
4. Click **Register app**
5. Download `GoogleService-Info.plist`
6. Place it in `iosApp/iosApp/GoogleService-Info.plist`

---

## Step 2: Understand the Architecture

### Why expect/actual Instead of a Multiplatform Library?

Many KMP Firebase libraries exist (e.g., GitLive's firebase-kotlin-sdk), but they:
- Add a large dependency tree
- May not support all Firebase features
- Can lag behind official SDK releases

Our approach: **bridge only what we need** using `expect/actual`. We wrap 4 methods:
- `logEvent()` — track user actions
- `setUserProperty()` — set persistent user attributes
- `recordException()` — crash reporting (Exercise 13b)
- `fetchWelcomeMessage()` — remote config (Exercise 13b)

### Project Structure

```
shared/src/
├── commonMain/kotlin/com/recall/app/analytics/
│   └── AnalyticsService.kt          ← Interface + expect AnalyticsProvider
│
├── androidMain/kotlin/com/recall/app/analytics/
│   └── AnalyticsService.android.kt  ← Android actual (Firebase SDK)
│
└── iosMain/kotlin/com/recall/app/analytics/
    └── AnalyticsService.ios.kt      ← iOS actual (CocoaPods Firebase)
```

---

## Step 3: The Common Interface (commonMain)

**File:** `shared/src/commonMain/kotlin/com/recall/app/analytics/AnalyticsService.kt`

```kotlin
interface AnalyticsService {
    fun logEvent(name: String, params: Map<String, Any>? = null)
    fun setUserProperty(name: String, value: String)
    fun recordException(throwable: Throwable)        // Exercise 13b
    suspend fun fetchWelcomeMessage(): String          // Exercise 13b
}

expect object AnalyticsProvider {
    fun get(): AnalyticsService
}
```

**Key decisions:**
- `logEvent` takes a Kotlin `Map<String, Any>` — each platform converts this to its native format
- `AnalyticsProvider` is an `expect object` — Android needs `initialize(context)`, iOS uses lazy initialization
- `recordException` and `fetchWelcomeMessage` are stubbed for 13b

---

## Step 4: Android Implementation

**File:** `shared/src/androidMain/kotlin/com/recall/app/analytics/AnalyticsService.android.kt`

### 4a. The Analytics Service

```kotlin
class AndroidAnalyticsService(context: Context) : AnalyticsService {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(name: String, params: Map<String, Any>?) {
        val bundle = params?.let { mapToBundle(it) }
        analytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }
    // ...
}
```

### 4b. Type Conversion: Map → Bundle

Firebase Android requires an `android.os.Bundle`, not a Kotlin `Map`. The conversion:

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

**Why this matters:** `Bundle` uses typed putters (putString, putInt, etc.). You can't just pass a Map. This conversion preserves type information for Firebase's analytics pipeline.

### 4c. The Provider (with Context)

```kotlin
actual object AnalyticsProvider {
    private lateinit var instance: AnalyticsService

    fun initialize(context: Context) {
        instance = AndroidAnalyticsService(context.applicationContext)
    }

    actual fun get(): AnalyticsService = instance
}
```

**Android requires `Context`** for Firebase initialization. Call `AnalyticsProvider.initialize(this)` in your `Application.onCreate()`.

### 4d. Gradle Dependencies

In `shared/build.gradle.kts`:
```kotlin
androidMain.dependencies {
    implementation("com.google.firebase:firebase-analytics-ktx:22.3.0")
}
```

In `composeApp/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.googleServices)        // Processes google-services.json
    alias(libs.plugins.firebaseCrashlytics)   // For Exercise 13b
}
```

---

## Step 5: iOS Implementation

**File:** `shared/src/iosMain/kotlin/com/recall/app/analytics/AnalyticsService.ios.kt`

### 5a. The Analytics Service

```kotlin
@OptIn(ExperimentalForeignApi::class)
class IOSAnalyticsService : AnalyticsService {
    override fun logEvent(name: String, params: Map<String, Any>?) {
        val nsParams = params?.let { mapToNSDictionary(it) }
        FIRAnalytics.logEventWithName(name, nsParams)
    }

    override fun setUserProperty(name: String, value: String) {
        FIRAnalytics.setUserPropertyString(value, forName = name)
    }
    // ...
}
```

**`@OptIn(ExperimentalForeignApi::class)`** is required because we're calling Objective-C APIs from Kotlin/Native via cinterop.

### 5b. Type Conversion: Map → NSDictionary

Firebase iOS expects `NSDictionary`, and Kotlin numbers must be wrapped in `NSNumber`:

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

**Why `NSNumber`?** Objective-C doesn't have primitive number types in collections — everything must be an object. `NSNumber` wraps primitives for `NSDictionary`.

### 5c. The Provider (Lazy)

```kotlin
@OptIn(ExperimentalForeignApi::class)
actual object AnalyticsProvider {
    private val instance: AnalyticsService by lazy {
        IOSAnalyticsService()
    }

    actual fun get(): AnalyticsService = instance
}
```

**iOS doesn't need `Context`** — Firebase initializes via `FirebaseApp.configure()` in the Swift AppDelegate, not from Kotlin.

### 5d. CocoaPods Integration

In `shared/build.gradle.kts`:
```kotlin
cocoapods {
    version = "1.0"
    pod("FirebaseAnalytics") {
        version = "~> 11.8.0"
        extraOpts += listOf("-compiler-option", "-fmodules")
    }
}
```

Run `./gradlew :shared:podInstall` to install the pods.

### 5e. Swift AppDelegate Initialization

In `iosApp/iosApp/iOSApp.swift`:
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

---

## Step 6: Using the Analytics Service

### Track Events

```kotlin
val analytics = AnalyticsProvider.get()

// Track a study session
analytics.logEvent("study_session_started", mapOf(
    "deck_id" to deckId,
    "card_count" to deck.cards.size
))

// Track a card flip
analytics.logEvent("card_flipped", mapOf(
    "deck_id" to deckId,
    "card_id" to cardId
))
```

### Set User Properties

```kotlin
analytics.setUserProperty("deck_count", decks.size.toString())
analytics.setUserProperty("user_tier", "free")
```

### Recommended Events

| Event Name | Parameters | Purpose |
|-----------|-----------|---------|
| `deck_created` | `deck_name`, `card_count` | Track content creation |
| `study_session_started` | `deck_id`, `card_count` | Track engagement |
| `study_session_completed` | `deck_id`, `cards_reviewed`, `duration_seconds` | Track completion |
| `card_flipped` | `deck_id`, `card_id` | Track interactions |
| `card_rated` | `deck_id`, `card_id`, `rating` | Track learning progress |

---

## Step 7: Testing and Verification

### Android — DebugView

1. Enable debug mode:
   ```bash
   adb shell setprop debug.firebase.analytics.app com.recall.app
   ```
2. Run the app and trigger events
3. Firebase Console → Analytics → **DebugView** — events appear in real-time

**Android Studio alternative:**
1. Go to **Run > Edit Configurations**
2. Add to VM options: `-Dfirebase.analytics.debug=true`

### iOS — DebugView

1. In Xcode, go to **Product > Scheme > Edit Scheme**
2. Select **Run** > **Arguments**
3. Add launch argument: `-FIRDebugEnabled`
4. Run the app and trigger events
5. Firebase Console → Analytics → **DebugView**

### Important: Dashboard Delay
Regular Firebase Analytics dashboard can take **24-48 hours** to show data. Always use **DebugView** for development and testing.

---

## How This Connects to the Course

```
Module 11: Signing & Versioning
    │   gradle.properties → version
    │   keystore.properties → signing
    │
Module 12: Fastlane Automation
    │   Fastfile → automated builds
    │
Module 13a: Firebase Analytics (THIS LESSON)
    │   expect/actual → cross-platform analytics
    │   logEvent() → track user behavior
    │   setUserProperty() → track user attributes
    │
Module 13b: Crashlytics & Remote Config (NEXT)
        recordException() → crash reporting
        fetchWelcomeMessage() → dynamic config
```

---

## Platform Comparison

| Aspect | Android | iOS |
|--------|---------|-----|
| SDK | Firebase Android SDK (Gradle) | Firebase iOS SDK (CocoaPods) |
| Config file | `google-services.json` | `GoogleService-Info.plist` |
| Initialization | `AnalyticsProvider.initialize(context)` in Application | `FirebaseApp.configure()` in AppDelegate |
| Map conversion | Map → `Bundle` (typed putters) | Map → `NSDictionary` (NSNumber wrappers) |
| Annotation | None | `@OptIn(ExperimentalForeignApi::class)` |
| Debug mode | `adb shell setprop` | `-FIRDebugEnabled` launch argument |

---

## File Locations Summary

| File | Path | Purpose |
|------|------|---------|
| Common interface | `shared/src/commonMain/.../analytics/AnalyticsService.kt` | Shared API + expect |
| Android actual | `shared/src/androidMain/.../analytics/AnalyticsService.android.kt` | Firebase Android |
| iOS actual | `shared/src/iosMain/.../analytics/AnalyticsService.ios.kt` | Firebase iOS via CocoaPods |
| Android config | `composeApp/google-services.json` | Firebase project config |
| iOS config | `iosApp/iosApp/GoogleService-Info.plist` | Firebase project config |
| Dependencies | `gradle/libs.versions.toml` | Firebase version catalog |
| CocoaPods | `shared/build.gradle.kts` | iOS Firebase pod declarations |

---

## Troubleshooting

### "AnalyticsProvider not initialized"
Call `AnalyticsProvider.initialize(this)` in your Application class `onCreate()` before accessing analytics.

### "google-services.json not found"
Download from Firebase Console → Project Settings → Your Apps → Android app → Download.
Place in `composeApp/google-services.json`.

### "ExperimentalForeignApi" compilation error
The `@OptIn(ExperimentalForeignApi::class)` annotation is required on the iOS implementation class AND the AnalyticsProvider. This is already included in the solution.

### "CocoaPods pod install failed"
```bash
./gradlew :shared:podInstall
```
If it fails, try:
```bash
cd shared && rm -rf build/cocoapods/ && cd ..
./gradlew :shared:podInstall
```

### Events not appearing in Firebase Console
- Use **DebugView** (not the main dashboard) for real-time testing
- Main dashboard takes 24-48 hours
- Verify config files have correct Firebase project ID

### iOS Simulator warnings (harmless)
`Failed to send CA Event` or `nw_socket_set_connection_idle` are iOS system warnings. They don't affect Firebase and can be ignored.

---

## Key Takeaways

1. **expect/actual > heavyweight libraries.** Bridge only what you need. 4 methods is lighter than an entire multiplatform Firebase SDK.
2. **Type conversion is the hard part.** Android needs `Bundle`, iOS needs `NSDictionary` with `NSNumber`. The shared interface hides this complexity.
3. **Initialization differs per platform.** Android needs `Context`, iOS uses lazy + AppDelegate. The `expect/actual` pattern handles this cleanly.
4. **Always use DebugView.** Firebase dashboard delays 24-48 hours. DebugView shows events in real-time.
5. **This foundation extends.** The same pattern adds Crashlytics and Remote Config in 13b — just implement the remaining interface methods.
