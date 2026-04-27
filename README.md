# ⌚ Exercise 7d: Wearable Data Layer API

**Branch Name:** `lesson-7d-wear-data-layer`
**Module:** Wearable Data Layer (Optional Deep Dive)
**Prerequisite:** Completed Exercise 7c
**Estimated Time:** 45 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will implement inter-device communication between a Wear OS watch and a phone using the **Wearable Data Layer API**. You'll use three core APIs: CapabilityClient (device discovery), MessageClient (one-off commands), and DataClient (persistent data sync).

### **📖 Background: When to Use the Data Layer API**
The Wearable Data Layer API enables communication between a phone and watch over Bluetooth. It's essential when:
- The watch app needs data from the phone app that isn't available via shared code
- You need to trigger actions on one device from another
- You want to sync state between paired devices

In our Recall app, we already share code via KMP/Koin, so the Data Layer isn't strictly needed for data access. However, it's a critical API to understand for real-world Wear OS development where code sharing isn't always possible.

### **📖 Key Concepts**
| API | Purpose | Persistence | Use Case |
| :--- | :--- | :--- | :--- |
| **CapabilityClient** | Find connected devices with specific features | N/A | "Is a phone with Recall installed connected?" |
| **MessageClient** | Send one-off commands | Not persisted | "Hey phone, refresh your data" |
| **DataClient** | Sync structured data | Persisted by Play Services | "Here's the current deck summary" |

### **✅ Task 1: Implement findPhoneNode() (TODO 1)**
1. **Open** `wear/src/main/java/com/recall/wear/datalayer/WearDataLayerService.kt`
2. **Implement** `findPhoneNode()` using CapabilityClient:
   - Call `capabilityClient.getCapability("recall_phone_app", CapabilityClient.FILTER_REACHABLE)`
   - Call `.await()` to convert the Task to a suspend result
   - Return `capabilityInfo.nodes.firstOrNull()`

### **✅ Task 2: Implement sendRefreshRequest() (TODO 2)**
1. **In the same file**, implement `sendRefreshRequest()` using MessageClient:
   - Call `findPhoneNode()` — if null, return early
   - Call `messageClient.sendMessage(node.id, "/recall/refresh", byteArrayOf())`
   - Call `.await()`

### **✅ Task 3: Implement syncDeckSummary() (TODO 3)**
1. **In the same file**, implement `syncDeckSummary()` using DataClient:
   - Create: `PutDataMapRequest.create("/recall/deck_summary")`
   - In `apply { }`: put "deck_name" (String), "card_count" (Int), "timestamp" (Long)
   - Convert: `.asPutDataRequest().setUrgent()`
   - Call: `dataClient.putDataItem(request).await()`

### **✅ Task 4: Implement onDataChanged() (TODO 4)**
1. **Open** `wear/src/main/java/com/recall/wear/datalayer/DataLayerListenerService.kt`
2. **Implement** `onDataChanged()`:
   - Iterate events, check `TYPE_CHANGED` and path "/recall/deck_summary"
   - Extract DataMap using `DataMapItem.fromDataItem(event.dataItem).dataMap`
   - Read "deck_name" and "card_count", log with `Log.d(TAG, ...)`

### **✅ Task 5: Implement onMessageReceived() (TODO 5)**
1. **In the same file**, implement `onMessageReceived()`:
   - Log the message path: `Log.d(TAG, "Message received: ${event.path}")`

### **✅ Task 6: Wire up the Sync Button (TODO 6)**
1. **Open** `wear/src/main/java/com/recall/wear/presentation/WearAppContent.kt`
2. **Find** the "Sync with Phone" Chip onClick handler
3. **Implement** the sync logic inside `coroutineScope.launch { }`:
   - Set `syncStatus = "Syncing..."`
   - Try: call `sendRefreshRequest()`, then `syncDeckSummary()` with first deck data
   - Catch: set `syncStatus = "No phone found"`
   - On success: set `syncStatus = "Synced!"`

### **🚀 Definition of Done**
* [ ] **TODO 1:** `findPhoneNode()` returns a Node when a phone is paired (or null otherwise)
* [ ] **TODO 2:** `sendRefreshRequest()` sends a message to the phone node
* [ ] **TODO 3:** `syncDeckSummary()` writes a DataItem with deck name, card count, and timestamp
* [ ] **TODO 4:** `onDataChanged()` logs received deck summary data from Logcat (filter: `DataLayerListener`)
* [ ] **TODO 5:** `onMessageReceived()` logs received message path
* [ ] **TODO 6:** Tapping "Sync with Phone" triggers the sync flow with status feedback

### **💡 Hints**
* All three client methods return `Task<T>` from Google Play Services. Use `kotlinx.coroutines.tasks.await()` to convert them to suspend functions
* `PutDataMapRequest.create(path)` creates a builder. Use `.apply { dataMap.putXxx() }` to add data
* Always include a timestamp in DataItems — without it, writing the same values won't trigger `onDataChanged()`
* Test with two emulators (phone + Wear OS) paired via the Wear OS companion app
* Filter Logcat by tag "DataLayerListener" to see received events
