# Android Release Automation with Fastlane — Step-by-Step Guide

## What This Lesson Teaches

This solution demonstrates how to automate the Android release pipeline using **Fastlane**, the industry-standard CI/CD tool for mobile apps. Instead of manually running Gradle commands, clicking through Android Studio dialogs, and remembering flags — you define the entire build process in a `Fastfile` and run it with a single command.

## Prerequisites

Before starting, ensure Module 11 (Production Release) is complete:
- `keystore.properties` exists in the project root with signing credentials
- `composeApp/build.gradle.kts` has signing config, R8, and ProGuard rules
- `gradle.properties` has `APP_VERSION_MAJOR/MINOR/PATCH` (single source of truth)

---

## Step 1: Install Fastlane

### Option A: RubyGems (Recommended)
```bash
sudo gem install fastlane -NV
```

### Option B: Homebrew (macOS)
```bash
brew install fastlane
```

### Verify
```bash
fastlane -v
```
Expected: `fastlane x.x.x` (any recent version)

---

## Step 2: Understand the Project Structure

Fastlane files live in `android/fastlane/`:

```
android/
└── fastlane/
    ├── Appfile      ← App identity (package name, API key path)
    └── Fastfile     ← Build automation lanes (the actual workflow)
```

### Appfile — App Identity
```ruby
# Package name (must match applicationId in build.gradle.kts)
package_name("com.recall.app")

# Google Play API key (needed only for upload — commented out in exercise)
json_key_file("fastlane_json_key.json")
```

### Fastfile — The Automation Pipeline
```ruby
default_platform(:android)

platform :android do
  lane :package_release do
    gradle(task: "clean")           # Step 1: Clean artifacts
    gradle(task: "bundleRelease")   # Step 2: Build signed AAB
    # upload_to_play_store(...)     # Step 3: Upload (commented out)
  end
end
```

**Key concept:** A "lane" is a named sequence of actions. `package_release` is our lane — it cleans, builds, and (optionally) uploads.

---

## Step 3: Run the Automation

### From Terminal

```bash
cd /path/to/Recall
fastlane android package_release
```

This executes the `package_release` lane which:
1. **`gradle(task: "clean")`** — Removes all previous build artifacts (`build/` directories)
2. **`gradle(task: "bundleRelease")`** — Builds the signed release AAB using:
   - Version from `gradle.properties` (single source of truth)
   - Signing credentials from `keystore.properties`
   - R8 code shrinking (`isMinifyEnabled = true`)
   - Resource shrinking (`isShrinkResources = true`)
   - ProGuard rules from `proguard-rules.pro`

### Expected Output

```
[01:00:00]: --- Step: gradle ---
[01:00:00]: $ ./gradlew clean
[01:00:05]: $ ./gradlew bundleRelease
[01:00:45]: ✅ Release bundle created successfully!
[01:00:45]: 📦 Bundle location: composeApp/build/outputs/bundle/release/composeApp-release.aab
```

### Verify the Output

```bash
# Check the AAB exists
ls -lh composeApp/build/outputs/bundle/release/composeApp-release.aab

# Check the version
$ANDROID_HOME/build-tools/35.0.0/aapt dump badging \
  composeApp/build/outputs/apk/release/composeApp-release.apk 2>/dev/null | grep version
```

---

## Step 4: Understand What Fastlane Replaces

| Manual Step | Fastlane Equivalent |
|------------|-------------------|
| Open Android Studio | Not needed |
| Build > Clean Project | `gradle(task: "clean")` |
| Build > Generate Signed Bundle > fill dialog > select release > Create | `gradle(task: "bundleRelease")` |
| Upload AAB to Play Console manually | `upload_to_play_store(...)` |
| Remember to use right keystore, flags, variant | All defined in Fastfile — consistent every time |

**The key benefit:** Whether you run this on your laptop, a teammate's machine, or a CI server (GitHub Actions, Bitrise, etc.), the result is identical. No "it works on my machine" problems.

---

## Step 5: The Upload Step (Future — Requires Play Console Account)

The upload step is commented out in the Fastfile. When ready to publish:

### Prerequisites for Upload
1. **Google Play Developer Account** ($25 one-time fee)
2. **API Key:** Go to Play Console > Settings > API access > Create service account > Download JSON key
3. Place the JSON key in `android/fastlane_json_key.json`

### Uncomment in Fastfile
```ruby
upload_to_play_store(
  track: 'internal',           # Upload to internal testing track
  release_status: 'draft',     # Create as draft (manual promotion)
  aab: lane_context[SharedValues::GRADLE_AAB_OUTPUT_PATH]
)
```

### Upload Tracks Explained
| Track | Purpose |
|-------|---------|
| `internal` | Internal team testing (up to 100 testers, instant availability) |
| `alpha` | Closed testing (invite-only testers) |
| `beta` | Open testing (anyone can join) |
| `production` | Public release |

The recommended flow: `internal` → `alpha` → `beta` → `production`

---

## Step 6: Running from Android Studio (Alternative)

If you prefer the IDE:

1. Open **Terminal** panel in Android Studio (bottom bar)
2. Run:
   ```bash
   fastlane android package_release
   ```
3. Watch the output in the Terminal panel

Alternatively, you can add a **Run Configuration**:
1. Go to **Run > Edit Configurations...**
2. Click **+** > **Shell Script**
3. Set:
   - Name: `Fastlane Release`
   - Script path: (leave empty)
   - Script text: `fastlane android package_release`
   - Working directory: project root
4. Click **OK**
5. Now you can run it from the Run dropdown like any other configuration

---

## How the Pipeline Connects to Module 11

```
gradle.properties (Version: 1.0.0)
    │
    ├──→ build.gradle.kts
    │    ├── versionCode = 1000000 (from gradle.properties)
    │    ├── versionName = "1.0.0" (from gradle.properties)
    │    ├── signingConfigs (from keystore.properties)
    │    ├── isMinifyEnabled = true (R8)
    │    └── isShrinkResources = true
    │
    └──→ Fastlane (Fastfile)
         ├── gradle(task: "clean")
         ├── gradle(task: "bundleRelease")  ← uses all the above
         └── upload_to_play_store (when ready)
              │
              └──→ Play Store (internal → alpha → beta → production)
```

---

## Build Output Locations

| Output | Path | Size |
|--------|------|------|
| Release AAB | `composeApp/build/outputs/bundle/release/composeApp-release.aab` | ~8 MB |
| Release APK | `composeApp/build/outputs/apk/release/composeApp-release.apk` | ~9.6 MB |
| R8 Mapping | `composeApp/build/outputs/mapping/release/mapping.txt` | Obfuscation map |

---

## Troubleshooting

### "fastlane: command not found"
Fastlane is not installed. Run:
```bash
sudo gem install fastlane -NV
# or
brew install fastlane
```

### "No signing config found" / Unsigned AAB
`keystore.properties` is missing or has wrong values. Verify:
```bash
cat keystore.properties
```
Must contain `storeFile`, `storePassword`, `keyAlias`, `keyPassword`.

### "Could not determine the dependencies of task ':composeApp:bundleRelease'"
Gradle sync issue. Run:
```bash
./gradlew --refresh-dependencies
```

### Build succeeds but AAB is missing
Check the exact output path. Fastlane prints it at the end:
```
📦 Bundle location: composeApp/build/outputs/bundle/release/composeApp-release.aab
```

### "json_key_file not found" warning
This is expected — the `fastlane_json_key.json` referenced in `Appfile` is only needed for the upload step (which is commented out). The warning doesn't affect the build.

---

## Key Takeaways

1. **Fastlane = reproducible builds.** Same command, same result, every time, everywhere.
2. **Lanes are composable.** You can create lanes for `debug`, `release`, `upload`, `test` — each combining different actions.
3. **Infrastructure before publishing.** The upload step is pre-built and commented out. When you're ready, it's one uncomment away.
4. **CI/CD ready.** This same Fastfile works on GitHub Actions, Bitrise, CircleCI — just install Fastlane in the CI environment and run the same command.
