# Release Signing Build Documentation

## Overview
This document covers the steps to generate a signed release build for the Recall app, including keystore generation, build configuration, and the resulting size optimizations from R8 code shrinking and resource stripping.

---

## Step 1: Generate a Keystore

```bash
keytool -genkey -v \
  -keystore recall-release.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias recall \
  -storepass <your_password> \
  -keypass <your_password> \
  -dname "CN=Recall App, OU=Development, O=Udacity, L=Mountain View, ST=CA, C=US"
```

**Output:** `recall-release.jks` in the project root.

**Important:** Never commit the keystore or passwords to version control. Both `*.jks` and `keystore.properties` are in `.gitignore`.

---

## Step 2: Configure Signing Credentials

The build script (`composeApp/build.gradle.kts`) reads credentials using Google's recommended approach: a separate `keystore.properties` file loaded with `java.util.Properties`, with environment variables as a fallback for CI/CD.

### Option A: keystore.properties File (Recommended for Local Development)

Create `keystore.properties` in the **project root**:

```properties
storePassword=your_keystore_password
keyPassword=your_key_password
keyAlias=recall
storeFile=/absolute/path/to/recall-release.jks
```

This file is loaded by `build.gradle.kts` using:
```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
```

**Why `keystore.properties` instead of `local.properties`?**
Google explicitly warns that `local.properties` is reserved for Android Gradle plugin properties (like `sdk.dir`). Putting custom values there can cause conflicts. A separate file keeps concerns separated and follows Google's official documentation.

### Option B: Environment Variables (Recommended for CI/CD)

```bash
export KEYSTORE_PATH=/path/to/recall-release.jks
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=recall
export KEY_PASSWORD=your_password
```

Environment variables are used as a fallback when `keystore.properties` is not present (e.g., on CI/CD servers like GitHub Actions).

### Priority Order
1. `keystore.properties` values are checked first
2. Environment variables are used as fallback
3. If neither is configured, the build produces an unsigned APK

---

## Step 3: Build the Release

### Signed APK (for direct install/testing)

```bash
./gradlew :composeApp:assembleRelease
```

**Output path:** `composeApp/build/outputs/apk/release/composeApp-release.apk`

### Signed App Bundle (for Google Play Store)

```bash
./gradlew :composeApp:bundleRelease
```

**Output path:** `composeApp/build/outputs/bundle/release/composeApp-release.aab`

---

## Step 4: Verify the Signature

```bash
# Using Android's apksigner (recommended)
$ANDROID_HOME/build-tools/35.0.0/apksigner verify --print-certs \
  composeApp/build/outputs/apk/release/composeApp-release.apk
```

Expected output:
```
Signer #1 certificate DN: CN=Recall App, OU=Development, O=Udacity, L=Mountain View, ST=CA, C=US
Signer #1 certificate SHA-256 digest: <hash>
```

Note: Use `apksigner` instead of `jarsigner`. Android uses v2/v3 APK signing scheme which `jarsigner` does not recognize.

---

## Build Size Comparison

| Build Type | Size | Path |
|-----------|------|------|
| **Debug APK** | 22.3 MB | `composeApp/build/outputs/apk/debug/composeApp-debug.apk` |
| **Release APK** | 9.5 MB | `composeApp/build/outputs/apk/release/composeApp-release.apk` |
| **Release AAB** | 7.9 MB | `composeApp/build/outputs/bundle/release/composeApp-release.aab` |

### Size Reduction: 57.3% (Debug APK vs Release APK)

---

## What Causes the Size Reduction?

### R8 Code Shrinking (`isMinifyEnabled = true`)
- Removes unused classes, methods, and fields from the app and its dependencies
- Renames remaining classes/methods to short names (obfuscation)
- Optimizes bytecode (inlining, dead code elimination)
- Particularly effective for large dependencies like Ktor, Room, and Compose where only a fraction of the library code is actually used

### Resource Shrinking (`isShrinkResources = true`)
- Removes unused drawables, layouts, strings, and other Android resources
- Works in tandem with R8 — if code referencing a resource is removed, the resource is also stripped

### AAB vs APK
- The App Bundle (AAB) is smaller than the APK because it's a publishing format, not an installable format
- Google Play generates optimized APKs from the AAB for each device configuration (screen density, CPU architecture, language)
- End users download only what their device needs, resulting in even smaller installs than the full APK

---

## ProGuard / R8 Rules

Located at: `composeApp/proguard-rules.pro`

Rules are configured to preserve:
- **Room:** Entity classes, DAOs, and database classes (uses annotation processing)
- **Koin:** DI module definitions and Kotlin metadata (uses reflection)
- **Kotlin Serialization:** Serializable classes and companion objects
- **Coroutines:** Main dispatcher factory and exception handlers
- **SLF4J:** Suppresses warnings about missing logging implementations

---

## File Locations Summary

| File | Path | Committed to Git? |
|------|------|--------------------|
| Keystore | `recall-release.jks` (project root) | No (`*.jks` in .gitignore) |
| Signing credentials | `keystore.properties` (project root) | No (`keystore.properties` in .gitignore) |
| Build config | `composeApp/build.gradle.kts` | Yes |
| ProGuard rules | `composeApp/proguard-rules.pro` | Yes |
| Debug APK | `composeApp/build/outputs/apk/debug/` | No (build output) |
| Release APK | `composeApp/build/outputs/apk/release/` | No (build output) |
| Release AAB | `composeApp/build/outputs/bundle/release/` | No (build output) |

---

## Troubleshooting

### "APK is unsigned"
- Signing credentials were not found
- Verify `keystore.properties` exists in the project root with correct values
- Or verify env vars are set: `echo $KEYSTORE_PATH`
- The build script falls back to an unsigned build if no credentials are provided

### "jarsigner says unsigned"
- This is expected. Use `apksigner verify` instead — Android uses v2/v3 signing, not JAR signing

### R8 breaks the app at runtime
- Check `proguard-rules.pro` for missing keep rules
- Common symptom: crash on database access (Room) or DI resolution (Koin)
- Add `-keep` rules for any classes accessed via reflection
