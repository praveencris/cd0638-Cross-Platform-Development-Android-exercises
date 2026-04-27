# 🏁 Solution 11: Production Release
**Branch Name:** `prod-sol-lesson-11-android-production-release-prep`

This branch contains the final production-ready configuration for the Recall app.

## Production Reference

This branch is a production-grade reference for the matching solution branch. It keeps the lesson scope intact while adding small runtime, release, or data-safety hardening where that feature exists.

Use the matching sol-* branch to study the original course solution; use this prod-sol-* branch to compare the production-ready version of the same lesson.


## 🛠️ Implementation Summary

### 0. Centralized Versioning
Version is defined in `gradle.properties` as the **single source of truth** for both Android and iOS:
```properties
APP_VERSION_MAJOR=1
APP_VERSION_MINOR=0
APP_VERSION_PATCH=0
```
`build.gradle.kts` reads from these properties to compute `versionCode` (1000000) and `versionName` ("1.0.0"). iOS syncs `MARKETING_VERSION` in `Config.xcconfig` from the same source. Build numbers (`versionCode` / `CURRENT_PROJECT_VERSION`) are independent per store.

### 1. Secure Signing
We implemented `signingConfigs` using a detached keystore.
* **Security:** Following Google's recommended approach, we load credentials from a separate `keystore.properties` file using `java.util.Properties`, with environment variables as a CI/CD fallback. This keeps sensitive data out of version control.
* **Why not `local.properties`?** Google explicitly reserves `local.properties` for the Android Gradle plugin (`sdk.dir`, etc.). Putting custom values there can cause conflicts.

#### keystore.properties (create in project root, never commit)
```properties
storePassword=your_keystore_password
keyPassword=your_key_password
keyAlias=recall
storeFile=/absolute/path/to/recall-release.jks
```

### 2. R8 & Resource Shrinking
* **Code Shrinking:** We enabled R8 (`isMinifyEnabled = true`), which removed unused code and renamed classes to obfuscate the logic.
* **Resource Stripping:** We enabled resource shrinking (`isShrinkResources = true`), which removed unused drawables and XML files.
* **Result:** Release APK is ~57% smaller than debug (22 MB → 9.6 MB). Release AAB is 8.0 MB.

### 3. ProGuard Stability
We added specific rules to `proguard-rules.pro` to support our KMP stack:
* **Koin:** Kept constructor metadata so DI doesn't fail at runtime.
* **Room:** Kept generated database implementations.
* **Serialization:** Kept `@Serializable` class metadata.

## 🧪 Verification
1. **Bundle Generation:** Successfully generated a signed App Bundle.
2. **Signature Verification:** Use `apksigner verify --print-certs` (not `jarsigner`) to confirm signing.
3. **Install Test:** Installed the release APK on a physical device to ensure R8 didn't break the database or network calls.
4. **Size Comparison:** Debug APK (22 MB) vs Release APK (9.6 MB) vs Release AAB (8.0 MB).

## 📄 Additional Documentation
See `release-sign-build.md` for detailed step-by-step instructions with both **terminal commands** and **Android Studio IDE alternatives** for each step — keystore generation, signing, building, APK analysis, and size comparison.
