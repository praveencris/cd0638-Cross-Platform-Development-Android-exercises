# 🌐 Exercise 3: Connecting to the Cloud

**Module:** Networking with Ktor & Serialization

**Estimated Time:** 20 Minutes

### **🎓 Objective**

In this exercise, you will connect the "Recall" app to the internet. You will set up the **Ktor HTTP Client**—the industry standard for KMP networking—and fetch a JSON file containing 10 decks and 50 flashcards from a remote server.

By the end of this lab, you will have:

1. Configured platform-specific networking engines for Android and iOS.
2. Initialized a global `HttpClient` with JSON serialization.
3. Mapped a remote JSON response to your existing `Deck` and `Card` domain models.
4. Verified the integration with automated tests.

---

### **🛠️ Prerequisites**

* Completion of Exercise 2 (Shared Business Logic).
* A working internet connection.

---

### **✅ Task 1: Configure the Network Stack**

KMP requires different "Engines" for different platforms to handle the underlying networking protocols.

1. **Version Catalog**: Open `gradle/libs.versions.toml`. Add the following under `[libraries]` (use version `2.3.7`):
* `ktor-client-core`
* `ktor-client-content-negotiation`
* `ktor-serialization-kotlinx-json`
* `ktor-client-okhttp` (for Android)
* `ktor-client-darwin` (for iOS)


2. **Build Script**: Open `shared/build.gradle.kts`.
* Add core and serialization libraries to `commonMain`.
* Add `ktor-client-okhttp` to `androidMain`.
* Add `ktor-client-darwin` to `iosMain`.


3. **Sync Gradle** to apply the changes.

---

### **✅ Task 2: Build the HttpClient**

Open `NetworkClient.kt` in the shared module. You need to initialize the client so it knows how to handle JSON data.

1. **Initialize**: Create the `HttpClient`.
2. **Install ContentNegotiation**: This allows the client to automatically convert JSON strings into Kotlin objects.
3. **Configure JSON**:
* Set `ignoreUnknownKeys = true`. This is critical because APIs often return extra metadata that isn't needed by our models.
* Set `prettyPrint = true` for easier debugging.



---

### **✅ Task 3: Fetch the Decks**

Open `RecallApiService.kt`. This class is responsible for the actual network call to your Gist.

1. **The Endpoint**: Use the following URL:
   `https://gist.githubusercontent.com/kshitizbali/7dba0064f63c3971d0194a6b5d938d56/raw/4b138fbc16292c86d2ada848738b0a706c29d5cc/decks.json`
2. **The Call**: Use `client.get(URL)` to fetch the data.
3. **The Result**: Return the response as a `List<Deck>`.

---

### **✅ Task 4: Verify the Fetch**

Before building a UI, we must ensure the network layer is robust.

1. **Open** the **Terminal** tab.
2. **Run** the tests:
```bash
./gradlew shared:test

```


3. **Verification**: The test should confirm that the `List<Deck>` is not empty and that the first deck contains the expected cards from your Gist.

---

### **🆘 Troubleshooting**

* **Android Internet Permission**: Ensure `<uses-permission android:name="android.permission.INTERNET" />` is in your `AndroidManifest.xml`.
* **Serialization Mismatch**: If you get a `SerializationException`, check that your `@SerialName` annotations in the `Card` model exactly match the keys in the JSON (e.g., `image_base64`).
* **iOS Engine**: Ensure you are using the `Darwin` engine for iOS; using `OkHttp` or `CIO` on iOS targets will cause a compile-time or runtime error.

---

### **🚀 Definition of Done**

* The Ktor client is successfully configured with platform-specific engines.
* The `RecallApiService` successfully fetches and parses all 10 decks.
* The integration tests pass, proving the JSON structure matches our shared models.
