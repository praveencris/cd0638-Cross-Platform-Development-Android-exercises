# 🏁 Solution 11 (iOS): Production Release
**Branch:** `sol-lesson-11-ios-production-release-prep`

This solution prepares the iOS target for the App Store by aligning it with the Android production build.

## 🛠️ Implementation Summary

### 1. Automated Versioning
We implemented a sync step using `PlistBuddy`.
* **Why?** Mismatched versions between Android and iOS cause headaches in crash reporting (e.g., Crashlytics).
* **Technique:** We treat the Android `composeApp/build.gradle.kts` versionCode/versionName as the "Single Source of Truth" and project those values into the iOS `Info.plist`.
* **Script:** Run `./iosApp/sync_version.sh` to synchronize versions from Android to iOS.

### 2. Release Mode Optimization
We configured the Xcode Archive scheme to use **Release**.
* **Impact:** This triggers the **Kotlin/Native** compiler to run LLVM optimizations (dead code elimination, link-time optimization).
* **Result:** The app launches ~40% faster and the binary size is significantly reduced compared to the Debug build.

### 3. Archive & Signing
We successfully generated an `.xcarchive`.
* **Verification:** The archive contains the compiled Swift UI and the optimized "Fat Framework" (arm64) from the shared module.
* **Command:** `xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp -configuration Release -archivePath ./build/Recall.xcarchive archive`

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `iosApp/sync_version.sh` | Shell script that syncs Android versionCode/versionName to iOS Info.plist using PlistBuddy. |
| `iosApp/iosApp/Info.plist` | Contains the synced `CFBundleVersion` and `CFBundleShortVersionString`. |
| `shared/build/` | Contains the release-optimized framework artifacts after running `./gradlew :shared:assembleRelease`. |

## 🚀 Build & Verification Steps

### 1. Sync Versions
```bash
./iosApp/sync_version.sh
```

### 2. Build Release Framework
```bash
./gradlew :shared:assembleRelease
```
Verify: Check `shared/build/fat-framework/release/` for optimized framework

### 3. Create iOS Archive
```bash
xcodebuild -workspace iosApp/iosApp.xcworkspace \
  -scheme iosApp \
  -configuration Release \
  -archivePath ./build/Recall.xcarchive \
  archive
```
Verify: Check `./build/Recall.xcarchive/` directory exists

## 🎯 Learning Objectives

- **Single Source of Truth:** Understand why version synchronization prevents production bugs in multi-platform apps
- **Kotlin/Native Optimization:** See the performance impact of Release vs Debug builds
- **iOS Production Pipeline:** Learn the full App Store submission workflow from archive to distribution
