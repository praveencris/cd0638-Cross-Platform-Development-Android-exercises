# iOS Release Build Documentation

## Overview
This document covers the steps to prepare the iOS Recall app for App Store submission, including version synchronization, Release mode optimization, and archiving. Each step includes both **command-line** and **IDE (Xcode / Android Studio)** instructions.

## Prerequisites & Limitations

> **Apple Developer Account ($99/year):** Archiving and code signing require a paid Apple Developer Program membership. Without one, the **archive step will fail** with "Signing requires a development team." However, all other steps — version sync, Debug and Release simulator builds, and size comparison — work without a Developer account. We document the full archive process below so you understand the complete pipeline, even if you cannot execute the final signing step.

---

## Step 1: Sync Versions from Android

Android `build.gradle.kts` is the single source of truth for versioning.

### Option A: Using the Sync Script (Terminal)

```bash
./iosApp/sync_version.sh
```

This extracts `versionCode` and `versionName` from `composeApp/build.gradle.kts` and writes them to `iosApp/iosApp/Info.plist`:
- `versionCode` (1) → `CFBundleVersion` (build number)
- `versionName` ("1.0") → `CFBundleShortVersionString` (user-facing version)

### Option B: Using Android Studio + Xcode (IDE)

**Read versions from Android Studio:**
1. Open the project in **Android Studio**
2. Navigate to `composeApp/build.gradle.kts`
3. Find `versionCode` and `versionName` in the `defaultConfig` block (around lines 62-63):
   ```
   versionCode = 1
   versionName = "1.0"
   ```
4. Note these values — you'll enter them in Xcode next

**Write versions in Xcode:**
1. Open `iosApp/iosApp.xcodeproj` in **Xcode**
2. In the Project Navigator (left sidebar), click on `iosApp` project (blue icon at the top)
3. Select the **iosApp** target
4. Go to the **General** tab
5. Under **Identity**, update:
   - **Version** → `1.0` (matches Android `versionName`)
   - **Build** → `1` (matches Android `versionCode`)

Alternatively, edit `Info.plist` directly:
1. In Xcode, open `iosApp/iosApp/Info.plist`
2. Find `CFBundleShortVersionString` → set to `1.0`
3. Find `CFBundleVersion` → set to `1`

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

## Step 2: Configure Release Scheme

### Option A: Using Xcode UI (Recommended)

1. Open `iosApp/iosApp.xcodeproj` in **Xcode**
2. In the menu bar, go to **Product > Scheme > Edit Scheme...**
3. In the left sidebar, select **Archive**
4. Change the **Build Configuration** dropdown from `Debug` to **`Release`**
5. Click **Close**

### Option B: Verify via Terminal

There's no direct terminal command to change the scheme — this is a Xcode UI-only setting. However, you can override the configuration when building from the command line:
```bash
xcodebuild ... -configuration Release ...
```

### Why This Matters
Release mode enables Kotlin/Native LLVM optimizations:
- Dead code elimination
- Function inlining
- Constant folding
- Debug symbol removal

---

## Step 3: Build the App

### Debug Build (for size comparison)

**Terminal:**
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Debug \
  -sdk iphonesimulator \
  -derivedDataPath ./build/ios-debug \
  -destination 'generic/platform=iOS Simulator' \
  build
```

**Xcode UI:**
1. In Xcode, select a **Simulator** destination from the device dropdown (top bar)
2. Go to **Product > Scheme > Edit Scheme...**
3. Under **Run**, ensure Build Configuration is **Debug**
4. Click **Close**, then **Product > Build** (or `Cmd+B`)

**Output:** `build/ios-debug/Build/Products/Debug-iphonesimulator/Recall.app`

### Release Build

**Terminal:**
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -sdk iphonesimulator \
  -derivedDataPath ./build/ios-release \
  -destination 'generic/platform=iOS Simulator' \
  build
```

**Xcode UI:**
1. Go to **Product > Scheme > Edit Scheme...**
2. Under **Run**, change Build Configuration to **Release**
3. Click **Close**, then **Product > Build** (or `Cmd+B`)
4. *Remember to switch back to Debug for development after comparing!*

**Output:** `build/ios-release/Build/Products/Release-iphonesimulator/Recall.app`

### Archive (for App Store / TestFlight)

> **Note:** This step requires an Apple Developer account (see Prerequisites above). The steps are documented for the complete learning experience.

**Terminal:**
```bash
xcodebuild -project iosApp/iosApp.xcodeproj \
  -scheme iosApp \
  -configuration Release \
  -archivePath ./build/Recall.xcarchive \
  archive
```

**Xcode UI (Recommended for Archive):**
1. In Xcode, select **Any iOS Device (arm64)** from the device dropdown (not a simulator — archives target real devices)
2. Ensure the Archive scheme is set to Release (Step 2 above)
3. Go to **Product > Archive**
4. Xcode compiles the app and Shared Framework in Release mode — wait for completion
5. When done, the **Organizer** window opens automatically (**Window > Organizer** if it doesn't)
6. Your archive appears under **Archives** — verify it's listed with the correct version number
7. From the Organizer you can:
   - **Distribute App** → Upload to App Store Connect / TestFlight
   - **Export** → Save an `.ipa` for ad-hoc distribution
   - **Validate App** → Check for submission issues without uploading

**Output:** Archive visible in Xcode Organizer, or `build/Recall.xcarchive/` if using terminal

---

## Build Size Comparison (Simulator Builds)

| Build Type | App Size (Simulator) | Binary Size |
|-----------|----------------------|-------------|
| **Debug** | 63 MB | 62 MB (Recall.debug.dylib) |
| **Release** | 39 MB | 39 MB (Recall binary) |

### Size Reduction: ~38% (Debug vs Release, Simulator)

> **Note:** These sizes are from simulator builds (x86_64/arm64 simulator architecture). Actual App Store sizes will differ — typically smaller due to App Thinning, which delivers only the device-specific architecture slice. The percentage reduction between Debug and Release remains comparable.

**How to check sizes in Xcode:**
- After archiving, open **Window > Organizer**
- Select your archive and click **Estimate Size** to see the estimated App Store download size

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
See Prerequisites at the top. To fix in Xcode: open the project, go to **Signing & Capabilities** tab, and select your **Team**. This requires an Apple Developer account.

### "Compile Kotlin Framework" runs every build
This is a known behavior — the Xcode build phase for Kotlin compilation doesn't use dependency analysis. The warning is informational and doesn't affect the build.

### Build succeeds in Xcode but fails from Terminal
Ensure `KONAN_DATA_DIR` is set correctly. Xcode uses the logged-in user's environment, but terminal sessions (especially as root) may have different paths.

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
    └──→ ./iosApp/sync_version.sh (or manual copy via Android Studio → Xcode)
         │
         └──→ iosApp/Info.plist
              CFBundleVersion = "1"
              CFBundleShortVersionString = "1.0"
              │
              └──→ iOS Archive / App Store
```

Always sync versions **before** archiving. The pattern: sync first, archive second.
