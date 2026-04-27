# 🏁 Solution 12b: iOS Packaging
**Branch:** `sol-lesson-12b-ios-appstore-publish`

This solution implements automated packaging for TestFlight and App Store distribution using Fastlane, the industry-standard CI/CD tool for mobile apps.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

### 1. The `build_app` (gym) Action
We replaced the manual "Product > Archive" workflow with the `build_app` command.

**Why?** Xcode's UI can change between versions, but the underlying command line tools (`xcodebuild` and `xcrun`) are stable and scriptable. This automation ensures that whoever runs the build (you, or a CI server like GitHub Actions) produces identical results every time.

**Result:** It produces standardized distribution files:
- **iosApp.ipa** - The distributable binary for TestFlight/App Store
- **iosApp.app.dSYM.zip** - Debug symbols for crash reporting (uploaded alongside the .ipa)

### 2. Version Management
We included `increment_build_number` to automatically bump the build version on each run.

**Benefit:** This solves the #1 error developers face when uploading to Apple: **"Duplicate Build Number"**

Every time you run `fastlane ios package_beta`, it increments the build number (1 → 2 → 3 → etc.) inside the Xcode project. You never have to manually change version numbers again.

### 3. Future-Proofing
Like the Android solution, we included the `upload_to_testflight` command but left it commented out.

**Benefit:** When you're ready to publish, you simply:
1. Enroll in Apple Developer Program ($99/year)
2. Create an App Store Connect API key
3. Remove the `#` comment character
4. Run the same command

You are now just one hashtag away from shipping to beta testers!

## 📂 Key Files

| File Path | Description |
| :--- | :--- |
| `iosApp/fastlane/Appfile` | Global Fastlane configuration containing app identifier and Apple ID |
| `iosApp/fastlane/Fastfile` | Lane definitions for automation workflows (increment version, build, upload) |
| `iosApp/iosApp.xcodeproj` | Xcode project containing build settings and configurations |
| `iosApp/Configuration/Config.xcconfig` | Build configuration with version numbers (CURRENT_PROJECT_VERSION, MARKETING_VERSION) |

## 🚀 Build & Verification Steps

### Prerequisites

**1. Install Fastlane:**
```bash
# Using RubyGems (recommended)
sudo gem install fastlane -NV

# Or using Homebrew (macOS)
brew install fastlane
```

**2. Verify installation:**
```bash
fastlane -v
```

**3. Install Xcode:**
- Xcode 15.0+ from the Mac App Store
- Accept Xcode license: `sudo xcodebuild -license accept`

### Running the Packaging Lane

**1. Navigate to project root:**
```bash
cd /Users/kshitizbali/AndroidStudioProjects/Recall
```

**2. Run the packaging lane:**
```bash
fastlane ios package_beta
```

**What happens:**
- Increments build number in `iosApp.xcodeproj`
- Compiles Swift/Kotlin code
- Archives the app with Release configuration
- Exports as .ipa with app-store provisioning
- Generates dSYM files for crash symbolication
- Displays output file locations

**3. Verify the output:**
```bash
ls -la iosApp.ipa
ls -la iosApp.app.dSYM.zip
```

Both files should be present in the project root directory.

## 🎯 Learning Objectives

After completing this module, you understand:

- **CI/CD Automation:** How Fastlane standardizes iOS app builds across development, testing, and production environments
- **Xcode CLI Tools:** How `xcodebuild` and `xcrun` work under the hood when you click "Archive" in Xcode
- **Version Management:** Why automatic build number incrementing prevents App Store submission errors
- **Distribution Formats:** The difference between .ipa (distributable) and .app (local simulator build)
- **Debug Symbols:** Why dSYM files are critical for crash reporting in production apps

## 📚 Additional Resources

- [Fastlane iOS Documentation](https://docs.fastlane.tools/getting-started/ios/setup/)
- [Apple Developer Program](https://developer.apple.com/programs/)
- [TestFlight Beta Testing Guide](https://developer.apple.com/testflight/)
- [App Store Connect API Keys](https://developer.apple.com/documentation/appstoreconnectapi/creating_api_keys_for_app_store_connect_api)
