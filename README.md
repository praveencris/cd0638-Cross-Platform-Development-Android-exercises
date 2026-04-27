# 📦 Exercise 11: Production Release Preparation
**Module:** Release & Deployment
**Estimated Time:** 45 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
It is time to ship! In this exercise, you will move beyond the "Debug" environment. You will configure the app to be smaller, faster, and cryptographically signed so it can be uploaded to the Google Play Store.

### **✅ Task 1: Create a Release Keystore**
Android apps must be signed with a digital certificate.
1. **Action:** In Android Studio, go to **Build > Generate Signed Bundle / APK**.
2. **Action:** Choose **Bundle**, then click **Create new** under Key store path.
3. **Action:** Save it as `release.keystore` in your project root (Note: Never commit this to Git in a real project!).

### **✅ Task 2: Configure Signing in Gradle**
1. **Open** `composeApp/build.gradle.kts`.
2. **Action**: Create a `keystore.properties` file in the project root to store your credentials (path, passwords, alias). This is Google's recommended approach — never use `local.properties` for custom values, and never hardcode passwords in the build script.
3. **Action**: In the build script, load `keystore.properties` using `java.util.Properties` and implement the `signingConfigs` block. Use environment variables as a fallback for CI/CD.
4. **Action**: Ensure the `release` build type uses this signing configuration.
5. **Action**: Add `keystore.properties`, `*.jks`, and `*.keystore` to `.gitignore`.

### **✅ Task 3: Optimization (R8)**
A debug APK is large and contains "leaking" info. We need to shrink it.
1. **Action**: Set `isMinifyEnabled = true` in the release build type.
2. **Action**: Set `isShrinkResources = true` to remove unused icons and layouts.

### **✅ Task 4: ProGuard Rules**
R8 is aggressive. It might "shrink" away code that Koin or Room needs to function via reflection.
1. **Open** `composeApp/proguard-rules.pro`.
2. **Action**: Add `-keep` rules for your `shared` module classes and third-party libraries.

### **🚀 Definition of Done**
* [ ] You can run `./gradlew :composeApp:bundleRelease` successfully.
* [ ] A signed `.aab` (Android App Bundle) file is generated in `composeApp/build/outputs/bundle/release/`.
* [ ] The release APK size is significantly smaller than the debug version.
