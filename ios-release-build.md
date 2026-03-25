# iOS Release Build Documentation

## Overview
This document covers the steps to prepare the iOS Recall app for App Store submission, including version synchronization, Release mode optimization, and archiving. Each step includes both **command-line** and **IDE (Xcode / Android Studio)** instructions.

## Prerequisites & Limitations

> **Apple Developer Account ($99/year):** Archiving and code signing require a paid Apple Developer Program membership. Without one, the **archive step will fail** with "Signing requires a development team." However, all other steps — version sync, Debug and Release simulator builds, and size comparison — work without a Developer account. We document the full archive process below so you understand the complete pipeline, even if you cannot execute the final signing step.

---

## Step 1: Sync Marketing Version from gradle.properties

`gradle.properties` is the **single source of truth** for the app version. The sync script reads the marketing version from there and writes it to iOS's `Config.xcconfig`. Build numbers are **independent per store** — the script does not touch them.

### Option A: Using the Sync Script (Terminal)

```bash
./iosApp/sync_version.sh
```

This reads `APP_VERSION_MAJOR`, `APP_VERSION_MINOR`, and `APP_VERSION_PATCH` from `gradle.properties` and writes `MARKETING_VERSION` to `iosApp/Configuration/Config.xcconfig`. It does **not** change `CURRENT_PROJECT_VERSION` (the iOS build number) — that increments independently per App Store submission.

### Option B: Using Android Studio + Xcode (IDE)

**Read version from Android Studio:**
1. Open the project in **Android Studio**
2. Open `gradle.properties` (project root)
3. Find the version properties:
   ```
   APP_VERSION_MAJOR=1
   APP_VERSION_MINOR=0
   APP_VERSION_PATCH=0
   ```
4. The marketing version is `1.0.0`

**Write marketing version in Xcode:**
1. Open `iosApp/Configuration/Config.xcconfig` in a text editor or Xcode
2. Update `MARKETING_VERSION` to match:
   ```
   MARKETING_VERSION=1.0.0
   ```
3. Leave `CURRENT_PROJECT_VERSION` as-is (independent build number)

Alternatively, in Xcode UI:
1. Open `iosApp/iosApp.xcodeproj` in **Xcode**
2. Select the **iosApp** target > **General** tab
3. Under **Identity**, update **Version** to `1.0.0`
4. Leave **Build** unchanged (independent per store)

### Verify
```bash
grep "MARKETING_VERSION" iosApp/Configuration/Config.xcconfig
```
Expected: `MARKETING_VERSION=1.0.0`

### Important: Marketing Version vs Build Number
- **Marketing version** (`MARKETING_VERSION` / `CFBundleShortVersionString`): User-facing, **must match across platforms**
- **Build number** (`CURRENT_PROJECT_VERSION` / `CFBundleVersion`): Internal, **independent per store**, must increment with every App Store submission
- Example: Marketing version 1.2.0 with iOS build numbers 5, 6, 7 across multiple submissions
- **Android-only hotfix?** Bump marketing version in gradle.properties for both, but only submit Android to Play Store. iOS build number stays put.

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
| Version source | `gradle.properties` | Single source of truth (APP_VERSION_MAJOR/MINOR/PATCH) |
| Version sync script | `iosApp/sync_version.sh` | Syncs MARKETING_VERSION from gradle.properties to Config.xcconfig |
| Config.xcconfig | `iosApp/Configuration/Config.xcconfig` | MARKETING_VERSION (synced) + CURRENT_PROJECT_VERSION (independent) |
| Xcode project | `iosApp/iosApp.xcodeproj` | iOS app project |
| Debug app (simulator) | `build/ios-debug/.../Recall.app` | Debug simulator build (63 MB) |
| Release app (simulator) | `build/ios-release/.../Recall.app` | Release simulator build (39 MB) |
| Archive | `build/Recall.xcarchive/` | Production archive for App Store |

---

## Cross-Platform Version Sync Workflow

```
gradle.properties (Single Source of Truth)
    │
    │  APP_VERSION_MAJOR=1
    │  APP_VERSION_MINOR=0
    │  APP_VERSION_PATCH=0
    │
    ├──→ Android build.gradle.kts
    │    versionName = "1.0.0" (computed from gradle.properties)
    │    versionCode = 1000000 (computed: MAJOR*1M + MINOR*1K + PATCH)
    │    │
    │    └──→ Android APK/AAB → Play Store
    │
    └──→ ./iosApp/sync_version.sh
         │
         └──→ iosApp/Configuration/Config.xcconfig
              MARKETING_VERSION = 1.0.0 (synced from gradle.properties)
              CURRENT_PROJECT_VERSION = 1 (independent — increments per App Store submission)
              │
              └──→ iOS Archive → App Store
```

Always sync marketing version **before** archiving. Build numbers increment independently per store.
