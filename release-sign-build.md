# Release Signing Build Documentation

## Overview
This document covers the steps to generate a signed release build for the Recall app, including keystore generation, build configuration, and the resulting size optimizations from R8 code shrinking and resource stripping. Each step includes both **terminal** and **Android Studio IDE** instructions.

---

## Step 1: Generate a Keystore

### Option A: Terminal (keytool)

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

### Option B: Android Studio IDE

1. In Android Studio, go to **Build > Generate Signed Bundle / APK...**
2. Select **Android App Bundle** or **APK**, click **Next**
3. Under Key store path, click **Create new...**
4. Fill in the dialog:
   - **Key store path:** Choose a location (e.g., project root or `~/keystores/recall-release.jks`)
   - **Password:** Enter a strong password
   - **Key alias:** `recall`
   - **Key password:** Enter a password (can match keystore password)
   - **Certificate:** Fill in organization details (CN, OU, O, L, ST, C)
5. Click **OK** — the keystore is created and automatically selected
6. Click **Cancel** (we'll configure Gradle signing separately rather than using the wizard)

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

**Using Android Studio to create the file:**
1. In the Project view, right-click the **project root** (top-level `Recall` folder)
2. Select **New > File**
3. Name it `keystore.properties`
4. Enter the four key-value pairs above
5. Save the file

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

### Debug Build (for size comparison)

**Terminal:**
```bash
./gradlew :composeApp:assembleDebug
```

**Android Studio IDE:**
1. In the toolbar, ensure the **build variant** is set to **debug** (Build > Select Build Variant, or use the Build Variants panel on the left sidebar)
2. Click the **Run** button (green play icon) or press `Shift+F10`
3. The debug APK is generated at `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Signed Release APK (for direct install/testing)

**Terminal:**
```bash
./gradlew :composeApp:assembleRelease
```

**Android Studio IDE:**
1. Go to **Build > Generate Signed Bundle / APK...**
2. Select **APK**, click **Next**
3. Select your keystore (or it auto-populates if you created one earlier):
   - **Key store path:** Browse to your `.jks` file
   - **Key store password:** Enter password
   - **Key alias:** `recall`
   - **Key password:** Enter password
4. Click **Next**
5. Select **release** build variant
6. Check **V1 (Jar Signature)** and **V2 (Full APK Signature)**
7. Click **Create**
8. Android Studio builds and signs the APK — a notification appears when done with a link to the output

**Output path:** `composeApp/build/outputs/apk/release/composeApp-release.apk`

### Signed App Bundle (for Google Play Store)

**Terminal:**
```bash
./gradlew :composeApp:bundleRelease
```

**Android Studio IDE:**
1. Go to **Build > Generate Signed Bundle / APK...**
2. Select **Android App Bundle**, click **Next**
3. Enter keystore credentials (same as APK above)
4. Click **Next**, select **release**, click **Create**

**Output path:** `composeApp/build/outputs/bundle/release/composeApp-release.aab`

---

## Step 4: Verify the Signature

### Terminal
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

### Android Studio IDE (Analyze APK)

1. Go to **Build > Analyze APK...**
2. Browse to `composeApp/build/outputs/apk/release/composeApp-release.apk`
3. Android Studio opens the APK Analyzer showing:
   - **File size** and **download size** (estimated Play Store download)
   - **classes.dex** — R8-optimized bytecode (you'll see obfuscated class names)
   - **res/** — remaining resources after shrinking
   - **META-INF/** — contains the signature files (confirms signing)
4. To compare debug vs release: click **Compare with previous APK...** button and select the debug APK

---

## Step 5: Size Comparison

### Terminal
Compare file sizes directly:
```bash
ls -lh composeApp/build/outputs/apk/debug/composeApp-debug.apk
ls -lh composeApp/build/outputs/apk/release/composeApp-release.apk
ls -lh composeApp/build/outputs/bundle/release/composeApp-release.aab
```

### Android Studio IDE (Analyze APK — Side-by-Side)

1. Go to **Build > Analyze APK...**
2. Open the **release** APK first
3. Click **Compare with previous APK...** (top-right button)
4. Select the **debug** APK
5. Android Studio shows a side-by-side breakdown:
   - Total size difference
   - Per-file size changes (which classes, resources, and libraries shrank the most)
   - dex file comparison (see how many classes R8 removed)

### Results

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

**Viewing R8 output in Android Studio:**
After a release build, check `composeApp/build/outputs/mapping/release/mapping.txt` for the R8 obfuscation map. This file maps obfuscated names back to original names — essential for reading crash reports from production.

---

## File Locations Summary

| File | Path | Committed to Git? |
|------|------|--------------------|
| Keystore | `recall-release.jks` (project root) | No (`*.jks` in .gitignore) |
| Signing credentials | `keystore.properties` (project root) | No (`keystore.properties` in .gitignore) |
| Build config | `composeApp/build.gradle.kts` | Yes |
| ProGuard rules | `composeApp/proguard-rules.pro` | Yes |
| R8 mapping | `composeApp/build/outputs/mapping/release/mapping.txt` | No (build output) |
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
- **In Android Studio:** Use **Build > Analyze APK** on the release APK to check if expected classes are present in the dex files
