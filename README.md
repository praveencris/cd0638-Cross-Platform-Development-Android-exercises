# 🍎 Exercise 11 (iOS): App Store Hardening
**Module:** Release & Deployment
**Prerequisite:** Module 11 (Android) completed
**Estimated Time:** 30 Minutes

## 🎓 Objective
The Android app is signed and optimized. Now, you must bring the iOS app up to the same standard. In this exercise, you will sync the version numbers and configure the Shared Framework for a "Release" build to ensure maximum performance.

## ✅ Task 1: Version Synchronization
The app version is defined in `gradle.properties` as the **single source of truth**. The marketing version (what users see) must match across Android and iOS. Build numbers are independent per store.

1. **Check:** Open `gradle.properties` and find `APP_VERSION_MAJOR=1`, `APP_VERSION_MINOR=0`, `APP_VERSION_PATCH=0`. The marketing version is `1.0.0`.
2. **Action:** Sync the marketing version to iOS by updating `MARKETING_VERSION` in `iosApp/Configuration/Config.xcconfig` to match.
   - **Important:** Only sync `MARKETING_VERSION`. Leave `CURRENT_PROJECT_VERSION` (build number) independent — it increments per App Store submission.

**How to update Config.xcconfig:**

### Option A: Manual Update (Recommended for Exercise)
1. Open `gradle.properties` and note the version: `1.0.0`
2. Open `iosApp/Configuration/Config.xcconfig`
3. Update `MARKETING_VERSION`:
   ```
   MARKETING_VERSION=1.0.0
   ```
4. Leave `CURRENT_PROJECT_VERSION=1` unchanged

### Option B: Using Xcode UI
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Select **iosApp** target > **General** tab
3. Update **Version** to `1.0.0` (matches gradle.properties)
4. Leave **Build** unchanged (independent per store)

### Option C: Automated Script (Stretch Goal)
Complete the TODOs in `iosApp/sync_version.sh` to create an automated version sync script that reads from `gradle.properties` and writes `MARKETING_VERSION` to `Config.xcconfig`.

## ✅ Task 2: Enable Release Optimizations
By default, the Shared Module compiles in "Debug" mode, which is slow and large. Let's configure it for production.

1. **Open** `iosApp/iosApp.xcworkspace` in Xcode
2. **Navigate** to **Product > Scheme > Edit Scheme...**
3. **Select** **Archive** in the left sidebar
4. **Change** **Build Configuration** to **Release**
5. **Close** the scheme editor

**Why this matters:** Release mode enables Kotlin/Native LLVM optimizations, resulting in:
- ~40% faster app launch
- Significantly smaller binary size
- Dead code elimination

## ✅ Task 3: Signing & Archiving
Now let's create a production archive that could be submitted to the App Store.

1. **Navigate** to the **Signing & Capabilities** tab in Xcode
2. **Select** your **Team** (you may need to add your Apple Developer account)
3. **Run** **Product > Archive**
4. **Verify** in the build logs that the Gradle task `:shared:embedAndSignAppleFrameworkForXcode` is called
5. **Verify** the Archive appears in the Organizer window (Window > Organizer)

**Troubleshooting:**
- If signing fails, ensure you have a valid Apple Developer account
- If the archive fails, check that the Release scheme configuration was saved
- Check build logs for any Kotlin/Native compilation errors

## 🚀 Definition of Done

Complete all three tasks and verify:

- [ ] The iOS `Info.plist` matches the Android version numbers (CFBundleVersion="1", CFBundleShortVersionString="1.0")
- [ ] The Kotlin Framework is compiled in Release mode (verified in build logs or by checking binary file size)
- [ ] Xcode successfully creates an Archive in the Organizer window

## 📚 Learning Objectives

After completing this exercise, you will understand:
- **Version Consistency:** Why maintaining synchronized versions across platforms prevents production bugs
- **Kotlin/Native Optimization:** The performance impact of Release vs Debug builds
- **iOS Production Pipeline:** The complete workflow from development to App Store submission

## 🔍 Verification

To verify your work:

1. **Check Info.plist:**
   ```bash
   /usr/libexec/PlistBuddy -c "Print :CFBundleVersion" iosApp/iosApp/Info.plist
   /usr/libexec/PlistBuddy -c "Print :CFBundleShortVersionString" iosApp/iosApp/Info.plist
   ```
   Should output: `1` and `1.0`

2. **Check Archive:**
   - Open Xcode > Window > Organizer
   - Verify "Recall" appears in the Archives list
   - Note the file size (Release should be smaller than Debug)

3. **Optional - Check Framework Size:**
   ```bash
   du -sh iosApp/build/XCFrameworks/release/shared.framework
   du -sh iosApp/build/XCFrameworks/debug/shared.framework
   ```
   Release should be significantly smaller than Debug.
