# iOS Release Build Documentation

## Overview
This document covers the steps to prepare the iOS Recall app for App Store submission, including version synchronization, Release mode optimization, and archiving.

## Prerequisites & Limitations

> **Apple Developer Account ($99/year):** Archiving and code signing require a paid Apple Developer Program membership. Without one, the **archive step will fail** with "Signing requires a development team." However, all other steps — version sync, Debug and Release simulator builds, and size comparison — work without a Developer account. We document the full archive process below so you understand the complete pipeline, even if you cannot execute the final signing step.

---

## Step 1: Sync Versions from Android

Android `build.gradle.kts` is the single source of truth for versioning. Run the sync script to update iOS `Info.plist`:

```bash
./iosApp/sync_version.sh
```

This extracts `versionCode` and `versionName` from `composeApp/build.gradle.kts` and writes them to `iosApp/iosApp/Info.plist`:
- `versionCode` (1) → `CFBundleVersion` (build number)
- `versionName` ("1.0") → `CFBundleShortVersionString` (user-facing version)

### Verify
```bash
/usr/libexec/PlistBuddy -c "Print :CFBundleVersion" iosApp/iosApp/Info.plist
/usr/libexec/PlistBuddy -c "Print :CFBundleShortVersionString" iosApp/iosApp/Info.plist
```
Expected: `1` and `1.0`

### Important Note on iOS Version Numbers
- `CFBundleVersion` (build number) must increment with every App Store submission, even if the version stays the same
- `CFBundleShortVersionString` (user-facing version) should stay synced with Android
- Example: version 1.2 with build numbers 5, 6, 7 across multiple submissions

---

## Step 2: Configure Release Scheme in Xcode

1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Go to **Product > Scheme > Edit Scheme...**
3. Select **Archive** in the left sidebar
4. Change **Build Configuration** to **Release**
5. Click **Close**

### Why This Matters
Release mode enables Kotlin/Native LLVM optimizations:
- Dead code elimination
- Function inlining
- Constant folding
- Debug symbol removal

---

## Step 3: Build the App

### Debug Build (for comparison)
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -sdk iphonesimulator \
  -derivedDataPath ./build/ios-debug \
  -destination 'generic/platform=iOS Simulator' \
  build
```

**Output:** `build/ios-debug/Build/Products/Debug-iphonesimulator/Recall.app`

### Release Build
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -sdk iphonesimulator \
  -derivedDataPath ./build/ios-release \
  -destination 'generic/platform=iOS Simulator' \
  build
```

**Output:** `build/ios-release/Build/Products/Release-iphonesimulator/Recall.app`

### Archive (for App Store / TestFlight)
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -archivePath ./build/Recall.xcarchive \
  archive
```

**Output:** `build/Recall.xcarchive/`

**Note:** This step requires an Apple Developer account (see Prerequisites above). The steps below document the full process for reference.

---

## Build Size Comparison (Simulator Builds)

| Build Type | App Size (Simulator) | Binary Size |
|-----------|----------------------|-------------|
| **Debug** | 63 MB | 62 MB (Recall.debug.dylib) |
| **Release** | 39 MB | 39 MB (Recall binary) |

### Size Reduction: ~38% (Debug vs Release, Simulator)

> **Note:** These sizes are from simulator builds (x86_64/arm64 simulator architecture). Actual App Store sizes will differ — typically smaller due to App Thinning, which delivers only the device-specific architecture slice. The percentage reduction between Debug and Release remains comparable.

---

## What Causes the Size Reduction?

### Kotlin/Native LLVM Optimizations (Release Mode)
- **Dead code elimination:** Removes unused functions and classes from the shared framework
- **Function inlining:** Replaces function calls with the function body for frequently called small functions
- **Constant folding:** Pre-computes constant expressions at compile time
- **Debug symbol removal:** Strips debug symbols and assertions

### Debug vs Release Binary Difference
- Debug includes `Recall.debug.dylib` (62 MB) — a separate debug dynamic library with full symbols
- Release compiles everything into a single optimized `Recall` binary (39 MB)
- Release also removes the `__preview.dylib` used for Xcode Previews

---

## Troubleshooting

### KLIB Resolver Duplicate Error
If you see "The same 'unique_name' found in more than one library" errors, it means Kotlin Native libraries exist in multiple locations (e.g., `/Users/you/.konan` and `/var/root/.konan`).

**Fix:**
```bash
sudo rm -rf /var/root/.konan
```

Or set the `KONAN_DATA_DIR` environment variable to point to a single location:
```bash
export KONAN_DATA_DIR=/Users/you/.konan
```

### Archive Fails with "Signing requires a development team"
See Prerequisites at the top. To fix, open the project in Xcode, go to **Signing & Capabilities**, and select your **Team**. This requires an Apple Developer account.

### "Compile Kotlin Framework" runs every build
This is a known behavior — the Xcode build phase for Kotlin compilation doesn't use dependency analysis. The warning is informational and doesn't affect the build.

---

## File Locations Summary

| File | Path | Description |
|------|------|-------------|
| Version sync script | `iosApp/sync_version.sh` | Syncs Android versions to iOS Info.plist |
| Info.plist | `iosApp/iosApp/Info.plist` | Contains CFBundleVersion and CFBundleShortVersionString |
| Xcode project | `iosApp/iosApp.xcodeproj` | iOS app project |
| Debug app (simulator) | `build/ios-debug/.../Recall.app` | Debug simulator build (63 MB) |
| Release app (simulator) | `build/ios-release/.../Recall.app` | Release simulator build (39 MB) |
| Archive | `build/Recall.xcarchive/` | Production archive for App Store |

---

## Cross-Platform Version Sync Workflow

```
Android build.gradle.kts (Single Source of Truth)
    │
    │  versionCode = 1
    │  versionName = "1.0"
    │
    ├──→ Android APK/AAB (uses these directly)
    │
    └──→ ./iosApp/sync_version.sh
         │
         └──→ iosApp/Info.plist
              CFBundleVersion = "1"
              CFBundleShortVersionString = "1.0"
              │
              └──→ iOS Archive / App Store
```

Always sync versions **before** archiving. The pattern: sync first, archive second.
