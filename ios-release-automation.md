# iOS Release Automation with Fastlane — Step-by-Step Guide

## What This Lesson Teaches

This solution demonstrates how to automate the iOS release pipeline using **Fastlane**, replacing the manual Xcode "Product > Archive" workflow. Instead of clicking through Xcode dialogs, remembering scheme settings, and manually incrementing build numbers — you define the entire process in a `Fastfile` and run it with a single command.

## Prerequisites

Before starting, ensure these are complete:
- Module 11 iOS (App Store Hardening): Release scheme configured, version sync working
- Module 12a (Android Packaging): Understand Fastlane concepts (lanes, actions)
- `gradle.properties` has `APP_VERSION_MAJOR/MINOR/PATCH` (single source of truth)
- `iosApp/Configuration/Config.xcconfig` has `MARKETING_VERSION` synced from `gradle.properties`

> **Apple Developer Account ($99/year):** The `build_app` step requires code signing, which requires an Apple Developer account. Without one, you can understand and study the pipeline but cannot execute the full build. The `increment_build_number` step works without an account.

---

## Step 1: Install Fastlane

### Option A: Homebrew (macOS)
```bash
brew install fastlane
```

### Option B: RubyGems
```bash
sudo gem install fastlane -NV
```

### Verify
```bash
fastlane -v
```

Also ensure Xcode CLI tools are installed:
```bash
xcode-select --install
sudo xcodebuild -license accept
```

---

## Step 2: Understand the Project Structure

Fastlane files for iOS live in `iosApp/fastlane/`:

```
iosApp/
├── fastlane/
│   ├── Appfile      ← App identity (bundle ID, Apple ID)
│   └── Fastfile     ← Build automation lanes
├── Configuration/
│   └── Config.xcconfig  ← Version numbers (MARKETING_VERSION, CURRENT_PROJECT_VERSION)
└── iosApp.xcodeproj     ← Xcode project
```

### Appfile — App Identity
```ruby
# Bundle ID (must match Config.xcconfig PRODUCT_BUNDLE_IDENTIFIER)
app_identifier("com.recall.app")

# Apple ID for App Store Connect (replace with your own)
apple_id("student@example.com")
```

### Fastfile — The Automation Pipeline
```ruby
platform :ios do
  lane :package_beta do
    increment_build_number(xcodeproj: "iosApp/iosApp.xcodeproj")  # Step 1
    build_app(scheme: "iosApp", export_method: "app-store")        # Step 2
    # upload_to_testflight(...)                                     # Step 3 (future)
  end
end
```

**Key concept:** The lane does three things — bump version, build archive, upload (when ready). Each is a Fastlane "action" that wraps complex Xcode CLI commands.

---

## Step 3: Understand Each Action

### Action 1: `increment_build_number`

```ruby
increment_build_number(
  xcodeproj: "iosApp/iosApp.xcodeproj"
)
```

**What it does:** Reads `CURRENT_PROJECT_VERSION` from the Xcode project, increments it by 1, and writes it back.

**Why it matters:** Apple rejects uploads with duplicate build numbers. If your last upload was build 5, the next must be 6 or higher. This action automates that so you never see the "Duplicate Build Number" error.

**Example:** Running the lane 3 times:
- Run 1: `CURRENT_PROJECT_VERSION` goes from 1 → 2
- Run 2: 2 → 3
- Run 3: 3 → 4

**Note:** This only changes the **build number** (internal). The **marketing version** (`MARKETING_VERSION`) stays synced with `gradle.properties` via `sync_version.sh`.

### Action 2: `build_app` (alias: `gym`)

```ruby
build_app(
  scheme: "iosApp",
  export_method: "app-store"
)
```

**What it does:** Replaces the entire Xcode archive workflow:
1. Compiles Swift and Kotlin/Native code
2. Links the shared framework
3. Creates an `.xcarchive` (same as Product > Archive)
4. Exports as `.ipa` (same as Distribute App > App Store)
5. Generates `.dSYM.zip` (debug symbols for crash reporting)

**Export methods explained:**

| Method | Purpose |
|--------|---------|
| `app-store` | Production-ready for TestFlight / App Store |
| `ad-hoc` | Direct install on registered devices |
| `development` | Development/debugging only |
| `enterprise` | Enterprise distribution (requires Enterprise account) |

**Output files:**
- `iosApp.ipa` — The distributable binary
- `iosApp.app.dSYM.zip` — Debug symbols (upload to Crashlytics for crash symbolication)

### Action 3: `upload_to_testflight` (Future)

```ruby
# upload_to_testflight(
#   skip_waiting_for_build_processing: true
# )
```

**What it does:** Uploads the `.ipa` to App Store Connect and distributes via TestFlight.

**Currently commented out** — requires Apple Developer Program enrollment.

---

## Step 4: Run the Automation

### From Terminal

```bash
cd iosApp
fastlane ios package_beta
```

**Important:** Run from `iosApp/` directory (where the `fastlane/` folder lives), not from the project root.

### Expected Output (with Apple Developer Account)

```
[01:00:00]: --- Step: increment_build_number ---
[01:00:01]: Successfully updated CURRENT_PROJECT_VERSION to 2
[01:00:01]: --- Step: build_app ---
[01:00:01]: Building scheme "iosApp" (Release configuration)
[01:02:30]: Successfully exported iosApp.ipa
[01:02:30]: ✅ iOS beta build created successfully!
[01:02:30]: 📦 Output files:
[01:02:30]:    - iosApp.ipa (distributable binary)
[01:02:30]:    - iosApp.app.dSYM.zip (debug symbols)
```

### Without Apple Developer Account

The `increment_build_number` step will succeed, but `build_app` will fail with a signing error. This is expected — you can still study the pipeline and understand what each step does.

---

## Step 5: Understand What Fastlane Replaces

| Manual Step in Xcode | Fastlane Equivalent |
|---------------------|-------------------|
| Open Xcode project | Not needed |
| General tab > Build > manually increment | `increment_build_number(...)` |
| Product > Scheme > Edit Scheme > Release | Handled by `build_app` configuration |
| Product > Archive (wait for compilation) | `build_app(scheme: "iosApp", ...)` |
| Organizer > Distribute App > App Store | `export_method: "app-store"` |
| Fill in signing dialogs | Configured in Appfile + Xcode project |
| App Store Connect > Upload | `upload_to_testflight(...)` |

**From 6+ manual steps to 1 command.** Same result, every time, on any machine.

---

## Step 6: Running from Xcode (Alternative)

If you prefer the IDE:

1. Open **Terminal** in Xcode (bottom panel)
2. Navigate to iosApp:
   ```bash
   cd iosApp
   ```
3. Run:
   ```bash
   fastlane ios package_beta
   ```

---

## How the Pipeline Connects to Previous Modules

```
gradle.properties (Version: 1.0.0)           ← Module 11: Single Source of Truth
    │
    └──→ sync_version.sh                     ← Module 11: Version Sync
         │
         └──→ Config.xcconfig
              MARKETING_VERSION = 1.0.0       ← Synced from gradle.properties
              CURRENT_PROJECT_VERSION = 4      ← Auto-incremented by Fastlane
              │
              └──→ Fastlane (Fastfile)        ← Module 12b: This lesson
                   ├── increment_build_number  → CURRENT_PROJECT_VERSION 4 → 5
                   ├── build_app               → iosApp.ipa + dSYM
                   └── upload_to_testflight    → App Store Connect (future)
                        │
                        └──→ TestFlight → App Store
```

---

## Comparison: Android (12a) vs iOS (12b) Pipelines

| Aspect | Android (12a) | iOS (12b) |
|--------|-------------|----------|
| Tool | Fastlane + Gradle | Fastlane + xcodebuild |
| Config dir | `android/fastlane/` | `iosApp/fastlane/` |
| Run command | `cd android && fastlane android package_release` | `cd iosApp && fastlane ios package_beta` |
| Build action | `gradle(task: "bundleRelease")` | `build_app(scheme: "iosApp")` |
| Output | `.aab` (App Bundle) | `.ipa` + `.dSYM.zip` |
| Version bump | Manual (via gradle.properties) | Auto (`increment_build_number`) |
| Upload | `upload_to_play_store` | `upload_to_testflight` |
| Account cost | $25 one-time (Play Console) | $99/year (Apple Developer) |
| Signing | `keystore.properties` | Xcode Signing & Capabilities |

---

## Output File Locations

| Output | Path | Purpose |
|--------|------|---------|
| IPA | `iosApp/iosApp.ipa` | Distributable binary for TestFlight/App Store |
| dSYM | `iosApp/iosApp.app.dSYM.zip` | Debug symbols for crash symbolication |
| Archive | `~/Library/Developer/Xcode/Archives/` | Xcode archive (also visible in Organizer) |

---

## Troubleshooting

### "fastlane: command not found"
```bash
brew install fastlane
# or
sudo gem install fastlane -NV
```

### "Could not find fastlane in current directory"
You're in the wrong directory. Run from `iosApp/`:
```bash
cd iosApp
fastlane ios package_beta
```

### "Signing requires a development team"
You need an Apple Developer account configured in Xcode:
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Go to **Signing & Capabilities**
3. Select your **Team**

### "Duplicate build number" on App Store Connect
This shouldn't happen with `increment_build_number` in the lane. If it does, manually check:
```bash
grep CURRENT_PROJECT_VERSION iosApp/Configuration/Config.xcconfig
```
And ensure it's higher than the last uploaded build.

### build_app fails with "No profiles found"
You need a provisioning profile for app-store distribution:
1. Open Xcode > Preferences > Accounts
2. Download manual profiles, or enable "Automatically manage signing"
3. Retry the lane

### "xcodebuild: error: The workspace is not found"
Ensure you're using `.xcodeproj` (not `.xcworkspace`) since there's no CocoaPods on this branch. If CocoaPods is present (later branches), use `.xcworkspace`.

---

## Key Takeaways

1. **One command replaces 6+ manual steps.** `fastlane ios package_beta` handles versioning, building, archiving, and exporting.
2. **Build numbers auto-increment.** No more "Duplicate Build Number" errors from App Store Connect.
3. **dSYM files are critical.** Always upload them alongside the `.ipa` for crash reporting (Crashlytics needs them to symbolicate stack traces).
4. **Same pattern as Android.** Both platforms use Fastlane with the same mental model — lanes, actions, progressive automation.
5. **Infrastructure is pre-built.** The TestFlight upload is one uncomment away when you have an Apple Developer account.
