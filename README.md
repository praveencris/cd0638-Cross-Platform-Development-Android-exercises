# ⌚ Exercise 7b: Wear OS Ecosystem

**Branch Name:** `lesson-7b-wear-os`
**Module:** Advanced Wear OS Integration
**Prerequisite:** Completed Module 7a (Basic List UI)
**Estimated Time:** 60 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will go beyond the app code and integrate Recall with the full Wear OS ecosystem. You will implement **Health Services** (Heart Rate), a **Complication**, and a **Tile**.

### **✅ Task 1: The Runtime Permission (Crucial)**
Wear OS apps require explicit user approval to read sensors. Merely adding it to the Manifest is not enough!
1.  **Open** `wear/src/main/java/com/recall/wear/presentation/WearApp.kt`.
2.  **Logic**:
    * Create a `rememberLauncherForActivityResult` for `ActivityResultContracts.RequestPermission()`.
    * Use a `LaunchedEffect` to launch the permission `Manifest.permission.BODY_SENSORS` as soon as the app starts.
    * Store the result (`isGranted`) in a state variable.

### **✅ Task 2: Health Services (Heart Rate)**
Now that we have permission, let's read the data.
1.  **Action**:
    * Inject `MeasureClient` (from Health Services).
    * **Crucial:** Only call `measureClient.measure(...)` if your permission state is `true`.
    * Display the value (e.g., "❤️ 72 BPM") at the top of your screen.

### **✅ Task 3: The "Due" Complication**
Users shouldn't have to open the app to see if they have work to do.
1.  **Open** `wear/src/main/java/com/recall/wear/complication/RecallComplicationService.kt`.
2.  **Action**: Override `onComplicationRequest`.
3.  **Logic**: Return a `ShortTextComplicationData`:
    * **Text:** "12" (Mock count)
    * **Title:** "Due"
    * **TapAction:** Opens the app.

### **✅ Task 4: The "Quick Study" Tile**
Tiles are swipeable widgets accessible from the watch face.
1.  **Open** `wear/src/main/java/com/recall/wear/tile/RecallTileService.kt`.
2.  **Action**: Override `tileRequest`.
3.  **Layout**: Build a ProtoLayout showing:
    * **Text:** "Physics Deck"
    * **Button:** "Study Now" (ClickAction opens the app).

### **🚀 Definition of Done**
* [ ] **Permissions:** On first launch, the app explicitly asks "Allow Recall to access sensor data?".
* [ ] **Health:** Once allowed, you see a live Heart Rate (mocked via Emulator controls).
* [ ] **Watch Face:** You can add the "Recall" complication to a slot.
* [ ] **Tiles:** You can swipe right on the watch face and see the "Study Now" tile.
