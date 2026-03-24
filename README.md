# 🏁 Solution 11 (iOS): Production Release
**Branch:** `sol-lesson-11-ios-production-release-prep`

This solution prepares the iOS target for the App Store by aligning it with the Android production build.

## 🛠️ Implementation Summary

### 1. Automated Versioning
We implemented a sync step using `PlistBuddy`.
* **Why?** Mismatched versions between Android and iOS cause headaches in crash reporting (e.g., Crashlytics).
* **Technique:** We treat the Android `composeApp/build.gradle.kts` versionCode/versionName as the "Single Source of Truth" and project those values into the iOS `Info.plist`.
* **Script:** Run `./iosApp/sync_version.sh` to synchronize versions from Android to iOS.
* **Note on `CFBundleVersion`:** This is the build number and must increment with every App Store submission, even if the user-facing version stays the same. Keep `CFBundleShortVersionString` synced with Android, but `CFBundleVersion` is iOS-specific and always goes up.

### 2. Release Mode Optimization
We configured the Xcode Archive scheme to use **Release**.
* **Impact:** This triggers the **Kotlin/Native** compiler to run LLVM optimizations (dead code elimination, function inlining, constant folding).
* **Result:** Debug app (63 MB) → Release app (39 MB) — a **38% size reduction**. Release also removes the separate debug dylib and Xcode Preview support library.

### 3. Archive & Signing
To generate an `.xcarchive` for App Store / TestFlight distribution:
* **Requirement:** An Apple Developer Team must be configured in Xcode's Signing & Capabilities tab.
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
| `iosApp/sync_version.sh` | Shell script that syncs Android versionCode/versionName to iOS Info.plist using PlistBuddy. |
| `iosApp/iosApp/Info.plist` | Contains the synced `CFBundleVersion` (1) and `CFBundleShortVersionString` (1.0). |
| `ios-release-build.md` | Detailed step-by-step release build documentation with troubleshooting. |

## 🚀 Build & Verification Steps

### 1. Sync Versions
```bash
./iosApp/sync_version.sh
```
Verify:
```bash
/usr/libexec/PlistBuddy -c "Print :CFBundleVersion" iosApp/iosApp/Info.plist        # → 1
/usr/libexec/PlistBuddy -c "Print :CFBundleShortVersionString" iosApp/iosApp/Info.plist  # → 1.0
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
See `ios-release-build.md` for detailed step-by-step instructions, troubleshooting (KLIB duplicates, signing issues), and the cross-platform version sync workflow diagram.
