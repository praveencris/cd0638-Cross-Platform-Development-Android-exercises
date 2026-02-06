# 🤖 Exercise 12a: Android Release Pipeline
**Module:** Publishing & Distribution
**Estimated Time:** 45 Minutes

## 🎓 Objective
Manual builds are risky and error-prone. In this exercise, you will create a **Reproducible Build Script** using Fastlane, the industry-standard CI/CD automation tool for mobile apps.

By the end, you will have a single command (`fastlane android package_release`) that generates a signed, production-ready `.aab` file every time, with consistent results across all environments.

## ✅ Task 1: Fastlane Initialization

**1. Install Fastlane:**

Choose one of the following installation methods:

**Option A: Using Homebrew (macOS, recommended)**
```bash
brew install fastlane
```

**Option B: Using RubyGems (all platforms)**
```bash
sudo gem install fastlane -NV
```

**2. Verify installation:**
```bash
fastlane -v
```

You should see version information displayed.

**3. Explore the fastlane directory:**
```bash
cd /Users/kshitizbali/AndroidStudioProjects/Recall/android
ls -la fastlane/
```

You should see:
- `Appfile` - Contains app configuration (package name, API credentials)
- `Fastfile` - Contains automation lanes (build workflows)
- `metadata/` - Contains Play Store listing information (optional)

## ✅ Task 2: Define the Build Logic

**1. Open `android/fastlane/Fastfile`**

You'll see a lane skeleton with TODOs. Your job is to implement the build automation.

**2. Complete TODO 2: Add gradle clean action**

Find the comment for TODO 2 and add:
```ruby
gradle(task: "clean")
```

This ensures a fresh build without cached artifacts.

**3. Complete TODO 3: Add gradle bundleRelease action**

Find the comment for TODO 3 and add:
```ruby
gradle(task: "bundleRelease")
```

This is the exact same command Android Studio runs behind the scenes to build a release bundle!

The `bundleRelease` task will:
- Apply signing configuration from Module 11
- Run R8 code shrinking and obfuscation
- Apply ProGuard rules for Koin, Room, and Serialization
- Strip unused resources
- Produce an optimized `.aab` file for Play Store

**4. Open `android/fastlane/Appfile`**

Complete TODO 4 by adding the package configuration:

```ruby
package_name("com.recall.app")
json_key_file("fastlane_json_key.json")
```

The package name must match the `applicationId` in `composeApp/build.gradle.kts`.

## ✅ Task 3: The "Dry Run"

**1. Navigate to project root:**
```bash
cd /Users/kshitizbali/AndroidStudioProjects/Recall
```

**2. Run the Fastlane packaging command:**
```bash
fastlane android package_release
```

**What you should see:**
- Fastlane starts and loads your configuration
- Gradle clean task runs
- Gradle bundleRelease task compiles and packages the app
- R8 optimization and ProGuard rules are applied
- Success message: "✅ Release bundle created successfully!"

**3. Verify the output:**
```bash
ls -la composeApp/build/outputs/bundle/release/
```

You should see: `composeApp-release.aab`

**4. Check the bundle size:**
```bash
du -sh composeApp/build/outputs/bundle/release/composeApp-release.aab
```

Typical size: 8-12 MB (optimized with R8 and resource shrinking from Module 11)

## 🚀 Definition of Done

Complete all tasks and verify:

- [ ] Fastlane is installed and accessible via command line
- [ ] `Fastfile` has gradle clean and bundleRelease actions implemented
- [ ] `Appfile` has package_name and json_key_file configured
- [ ] You can run `fastlane android package_release` without errors
- [ ] A signed App Bundle (`.aab`) is generated automatically in `composeApp/build/outputs/bundle/release/`
- [ ] The bundle is optimized (file size < 15 MB)

## 📚 Learning Objectives

After completing this exercise, you will understand:

- **CI/CD Automation:** How Fastlane standardizes mobile app builds across development and production environments
- **Reproducible Builds:** Why automation eliminates human error and ensures consistent results
- **Build Orchestration:** How to chain multiple Gradle tasks into a single command
- **Android App Bundles:** How `.aab` files enable Play Store to generate optimized APKs for each device configuration
- **Infrastructure as Code:** How to define build workflows that work identically on local machines and CI servers

## 🔍 Verification Commands

To verify your implementation:

**1. Check Fastfile syntax:**
```bash
cd android
fastlane android package_release --verbose
```

**2. Verify bundle signature:**
```bash
jarsigner -verify -verbose -certs composeApp/build/outputs/bundle/release/composeApp-release.aab
```

Should show: "jar verified"

**3. Inspect bundle contents:**
```bash
bundletool build-apks --bundle=composeApp/build/outputs/bundle/release/composeApp-release.aab --output=recall.apks --mode=universal
unzip -l recall.apks
```

## 🎯 Next Steps

After completing this exercise:
- **CI/CD Integration:** Connect Fastlane to GitHub Actions or Jenkins for automated releases
- **Play Store Upload:** Create a Google Play Developer Account and enable the commented-out `upload_to_play_store` action
- **Beta Testing:** Set up internal/closed testing tracks for QA validation
- **Release Management:** Implement versioning strategies and changelog automation

## 💡 Troubleshooting

**Issue:** "Fastlane not found"
- **Solution:** Ensure Fastlane is in your PATH, or use full path: `/usr/local/bin/fastlane`

**Issue:** "Gradle task failed"
- **Solution:** Run `./gradlew clean` manually first, then try again

**Issue:** "Signing configuration not found"
- **Solution:** Ensure Module 11 signing setup is complete (check `composeApp/build.gradle.kts` for signingConfigs)

**Issue:** "Bundle not found after build"
- **Solution:** Check Gradle output for errors, ensure `bundleRelease` task completed successfully
