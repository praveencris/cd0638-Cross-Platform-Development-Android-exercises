# 🍎 Exercise 12b: iOS Release Pipeline
**Module:** Publishing & Distribution
**Estimated Time:** 45 Minutes

## 🎓 Objective
Archiving in Xcode takes many manual clicks: Product → Archive → Distribute → Wait → Export. In this exercise, you will automate the creation of the `.ipa` file using Fastlane, the industry-standard CI/CD automation tool for iOS apps.

By the end, you will have a single command (`fastlane ios package_beta`) that generates a signed, production-ready `.ipa` file every time, with consistent results across all environments.

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
cd /Users/kshitizbali/AndroidStudioProjects/Recall/iosApp
ls -la fastlane/
```

You should see:
- `Appfile` - Contains app configuration (bundle ID, Apple ID)
- `Fastfile` - Contains automation lanes (build workflows)

## ✅ Task 2: Appfile Configuration

**1. Open `iosApp/fastlane/Appfile`**

You'll see TODO 5 asking you to configure the app identifier and Apple ID.

**2. Complete TODO 5: Add app_identifier**

Find the comment for TODO 5 and add:
```ruby
app_identifier("com.recall.app")
```

This must match the bundle identifier in your Xcode project. You can verify it in `iosApp/Configuration/Config.xcconfig` (look for `PRODUCT_BUNDLE_IDENTIFIER`).

**3. Complete TODO 5: Add apple_id**

Below the app_identifier, add:
```ruby
apple_id("student@example.com")
```

Even without a paid Apple Developer Program account, Fastlane needs to know who is attempting to build and sign the app. In production, you would replace this with your real Apple ID email.

## ✅ Task 3: Define the Build Logic

**1. Open `iosApp/fastlane/Fastfile`**

You'll see a file with 4 TODOs. Your job is to implement the build automation by following each TODO in sequence.

**2. Complete TODO 1: Define the iOS platform block**

At the top of the file, add:
```ruby
platform :ios do
```

And at the very end of the file (after the last TODO), close the block with:
```ruby
end
```

All iOS-specific lanes will be defined inside this platform block.

**3. Complete TODO 2: Define the package_beta lane**

Inside the platform block, add:
```ruby
lane :package_beta do
```

And after TODO 4 is completed, close the lane with:
```ruby
end
```

This lane will contain the build automation steps.

**4. Complete TODO 3: Add increment_build_number**

Inside the `package_beta` lane, add:
```ruby
increment_build_number(xcodeproj: "iosApp/iosApp.xcodeproj")
```

**Why is this important?** Apple rejects uploads with duplicate build numbers. By automatically incrementing the build number on each run (1 → 2 → 3 → etc.), you'll never encounter the dreaded "Duplicate Build Number" error.

**5. Complete TODO 4: Add build_app action**

Below the increment_build_number line, add:
```ruby
build_app(
  scheme: "iosApp",
  export_method: "app-store"
)
```

This is the exact same command Xcode runs behind the scenes when you click "Product → Archive"!

The `build_app` action (also known as `gym`) will:
- Compile your Swift and Kotlin code
- Archive the app with Release configuration
- Export as .ipa with app-store provisioning
- Generate .dSYM files for crash symbolication
- Save output files in the project root directory

## ✅ Task 4: The "Dry Run"

**1. Navigate to project root:**
```bash
cd /Users/kshitizbali/AndroidStudioProjects/Recall
```

**2. Run the Fastlane packaging command:**
```bash
fastlane ios package_beta
```

**What you should see:**
- Fastlane starts and loads your configuration
- Build number increments (check console output)
- Xcode build process runs (compile, link, archive)
- Export process creates .ipa and .dSYM files
- Success message: "✅ iOS beta build created successfully!"

**3. Verify the output:**
```bash
ls -la iosApp.ipa
ls -la iosApp.app.dSYM.zip
```

Both files should be present in the project root directory.

**4. Check the file sizes:**
```bash
du -sh iosApp.ipa
du -sh iosApp.app.dSYM.zip
```

Typical sizes:
- .ipa: 15-25 MB (contains compiled app binary and resources)
- .dSYM.zip: 5-15 MB (debug symbols for crash reporting)

## 🚀 Definition of Done

Complete all tasks and verify:

- [ ] Fastlane is installed and accessible via command line
- [ ] `Appfile` has app_identifier and apple_id configured
- [ ] `Fastfile` has platform :ios block defined
- [ ] `Fastfile` has package_beta lane defined
- [ ] `Fastfile` includes increment_build_number action
- [ ] `Fastfile` includes build_app action with correct parameters
- [ ] You can run `fastlane ios package_beta` without errors
- [ ] An .ipa file is generated automatically in the project root
- [ ] A .dSYM.zip file is generated alongside the .ipa

## 📚 Learning Objectives

After completing this exercise, you will understand:

- **CI/CD Automation:** How Fastlane standardizes iOS builds across development and production environments
- **Reproducible Builds:** Why automation eliminates human error and ensures consistent results
- **Build Orchestration:** How to chain multiple Xcode operations into a single command
- **Version Management:** Why automatic build number incrementing prevents App Store submission errors
- **Distribution Formats:** The difference between .ipa (distributable) and .app (simulator build)
- **Debug Symbols:** Why dSYM files are critical for crash reporting in production apps

## 🔍 Verification Commands

To verify your implementation:

**1. Check Fastfile syntax:**
```bash
cd iosApp
fastlane ios package_beta --verbose
```

**2. Inspect .ipa contents:**
```bash
unzip -l iosApp.ipa | head -20
```

You should see the app bundle structure with compiled binaries, frameworks, and resources.

**3. Verify dSYM structure:**
```bash
unzip -l iosApp.app.dSYM.zip
```

You should see debug symbol files (.dSYM directories) used for crash symbolication.

**4. Check build number increment:**
```bash
/usr/libexec/PlistBuddy -c "Print :CFBundleVersion" iosApp/iosApp/Info.plist
```

Run `fastlane ios package_beta` again and check the build number - it should increment by 1.

## 🎯 Next Steps

After completing this exercise:
- **TestFlight Distribution:** Enroll in Apple Developer Program ($99/year) and enable `upload_to_testflight`
- **Beta Testing:** Invite internal and external testers through App Store Connect
- **CI/CD Integration:** Connect Fastlane to GitHub Actions or Jenkins for automated releases
- **Release Management:** Implement versioning strategies and changelog automation

## 💡 Troubleshooting

**Issue:** "Fastlane not found"
- **Solution:** Ensure Fastlane is in your PATH, or use full path: `/usr/local/bin/fastlane`

**Issue:** "xcodebuild: error: Unable to find a destination matching the provided destination specifier"
- **Solution:** Open Xcode, select a simulator or device, then try again

**Issue:** "User interaction is not allowed"
- **Solution:** This usually means you need to unlock your keychain. Run: `security unlock-keychain`

**Issue:** "Code signing error"
- **Solution:** For this exercise, you can use export_method: "development" instead of "app-store" if you don't have provisioning profiles set up

**Issue:** "Build number not incrementing"
- **Solution:** Check that the xcodeproj path is correct in the increment_build_number action

## 📖 Additional Resources

- [Fastlane iOS Documentation](https://docs.fastlane.tools/getting-started/ios/setup/)
- [Fastlane gym (build_app) Reference](https://docs.fastlane.tools/actions/build_app/)
- [Apple Developer Program](https://developer.apple.com/programs/)
- [TestFlight Beta Testing Guide](https://developer.apple.com/testflight/)

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.

