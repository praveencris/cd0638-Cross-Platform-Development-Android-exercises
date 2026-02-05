# 📦 Exercise 11: Production Release Preparation
**Module:** Release & Deployment
**Estimated Time:** 45 Minutes

### **🎓 Objective**
It is time to ship! In this exercise, you will move beyond the "Debug" environment. You will configure the app to be smaller, faster, and cryptographically signed so it can be uploaded to the Google Play Store.

### **✅ Task 1: Create a Release Keystore**
Android apps must be signed with a digital certificate.
1. **Action:** In Android Studio, go to **Build > Generate Signed Bundle / APK**.
2. **Action:** Choose **Bundle**, then click **Create new** under Key store path.
3. **Action:** Save it as `release.keystore` in your project root (Note: Never commit this to Git in a real project!).

### **✅ Task 2: Configure Signing in Gradle**
1. **Open** `composeApp/build.gradle.kts`.
2. **Action**: Implement the `signingConfigs` block. Reference your new keystore file and the passwords you created.
3. **Action**: Ensure the `release` build type uses this signing configuration.

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
