# 🏁 Solution 11 (iOS): Production Release
**Branch Name:** `sol-lesson-11-ios-production-release-prep`
This solution prepares the iOS target for the App Store by aligning it with the Android production build.

> **Prerequisite:** Archiving and code signing require a paid Apple Developer Program membership ($99/year). Without one, version sync and Debug/Release simulator builds still work — only the final archive step requires the account. All steps are documented below for the complete learning experience.

## Branch Context

This is the completed solution branch for the matching exercise. Use it after attempting the lesson branch to compare implementation choices and verify your work.

For production-hardened code, compare this branch with its matching prod-sol-* branch when one exists.


## 🛠️ Implementation Summary

### 1. Centralized Versioning (Strategy B)
Version is defined in `gradle.properties` as the **single source of truth**:
```properties
APP_VERSION_MAJOR=1
APP_VERSION_MINOR=0
APP_VERSION_PATCH=0
```
* **Marketing version** (`MARKETING_VERSION` in Config.xcconfig) is synced from `gradle.properties` — must match across Android and iOS.
* **Build numbers** (`CURRENT_PROJECT_VERSION` in Config.xcconfig / `versionCode` in build.gradle.kts) are **independent per store** — iOS increments per App Store submission, Android increments per Play Store submission.
* **Script:** Run `./iosApp/sync_version.sh` to sync marketing version from `gradle.properties` to `Config.xcconfig`.
* **Why not sync build numbers?** If you only fix an Android bug, bump the shared marketing version but only submit Android. iOS's build number stays put until iOS actually ships.

### 2. Release Mode Optimization
We configured the Xcode Archive scheme to use **Release**.
* **Impact:** This triggers the **Kotlin/Native** compiler to run LLVM optimizations (dead code elimination, function inlining, constant folding).
* **Result:** Debug app (63 MB) → Release app (39 MB) — a **38% size reduction**. Release also removes the separate debug dylib and Xcode Preview support library.

### 3. Archive & Signing
To generate an `.xcarchive` for App Store / TestFlight distribution (requires Apple Developer account — see prerequisite above):
* **Command:**
  ```bash
  xcodebuild -project iosApp/iosApp.xcodeproj \
    -scheme iosApp \
    -configuration Release \
    -archivePath ./build/Recall.xcarchive \
    archive
  ```

## 📊 Build Size Comparison (Simulator Builds)

| Build Type | App Size (Simulator) | Notes |
| :--- | :--- | :--- |
| **Debug** | 63 MB | Includes debug dylib (62 MB) + preview support |
| **Release** | 39 MB | Single optimized binary, no debug symbols |

> Sizes are from simulator builds. Actual App Store sizes will be smaller due to App Thinning.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `gradle.properties` | Single source of truth for APP_VERSION_MAJOR/MINOR/PATCH. |
| `iosApp/sync_version.sh` | Syncs MARKETING_VERSION from gradle.properties to Config.xcconfig. |
| `iosApp/Configuration/Config.xcconfig` | Contains MARKETING_VERSION (synced) and CURRENT_PROJECT_VERSION (independent). |
| `ios-release-build.md` | Detailed step-by-step release build documentation with troubleshooting. |

## 🚀 Build & Verification Steps

### 1. Sync Versions
```bash
./iosApp/sync_version.sh
```
Verify:
```bash
grep "MARKETING_VERSION" iosApp/Configuration/Config.xcconfig  # → MARKETING_VERSION=1.0.0
```

### 2. Build Release App
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -sdk iphonesimulator \
  -derivedDataPath ./build/ios-release \
  -destination 'generic/platform=iOS Simulator' \
  build
```
Verify: `build/ios-release/Build/Products/Release-iphonesimulator/Recall.app` exists

### 3. Create iOS Archive (requires Apple Developer Team)
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -archivePath ./build/Recall.xcarchive \
  archive
```
Verify: `./build/Recall.xcarchive/` directory exists. Check archive in Xcode Organizer (Window > Organizer).

## 🎯 Learning Objectives

- **Single Source of Truth:** Understand why version synchronization prevents production bugs in multi-platform apps
- **Kotlin/Native Optimization:** See the performance impact of Release vs Debug builds (63 MB → 39 MB)
- **iOS Production Pipeline:** Learn the full App Store submission workflow from archive to distribution

## 📄 Additional Documentation
See `ios-release-build.md` for detailed step-by-step instructions with both **terminal commands** and **IDE (Xcode / Android Studio) alternatives** for each step, troubleshooting (KLIB duplicates, signing issues), and the cross-platform version sync workflow diagram.
