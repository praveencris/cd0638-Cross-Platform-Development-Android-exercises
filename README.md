# 🏁 Solution 7d: Wearable Data Layer API
**Branch:** `sol-lesson-7d-wear-data-layer`
**Prerequisite:** Completed Exercise 7c

This branch demonstrates the **Wearable Data Layer API** for inter-device communication between a phone app and a Wear OS watch app over Bluetooth. It implements all three core APIs: DataClient, MessageClient, and CapabilityClient.

> **Note:** This is a demonstration/sample branch. In the real Recall architecture, the watch app accesses shared KMP code directly via Koin, making the Data Layer unnecessary for data access. This branch exists purely to teach the Data Layer API pattern that is essential for apps where code sharing isn't possible.

## 🛠️ Implementation Summary

### 1. WearDataLayerService — Core API Wrapper
A helper class that initializes and wraps the three Data Layer clients:
* **CapabilityClient** (`findPhoneNode()`): Discovers connected devices that advertise the "recall_phone_app" capability. Uses `FILTER_REACHABLE` to only find currently connected devices.
* **MessageClient** (`sendRefreshRequest()`): Sends a one-off "/recall/refresh" message to the phone. Messages are fire-and-forget — delivered once, not persisted. Ideal for triggering actions.
* **DataClient** (`syncDeckSummary()`): Writes a DataItem to path "/recall/deck_summary" containing deck name, card count, and timestamp. DataItems are automatically synced to all connected devices and persisted by Google Play Services.

### 2. DataLayerListenerService — Event Receiver
A `WearableListenerService` that runs in the background:
* **onDataChanged():** Receives DataItem updates at "/recall/deck_summary" and logs the deck name + card count
* **onMessageReceived():** Receives messages and logs the path
* Registered in AndroidManifest.xml with intent filters for DATA_CHANGED and MESSAGE_RECEIVED

### 3. UI Integration — Sync Chip
A "Sync with Phone" Chip added to the main deck list:
* Sends a refresh message via MessageClient
* Syncs current deck summary via DataClient
* Shows status feedback: "Syncing..." → "Synced!" or "No phone found"

### Architecture: Phone ↔ Watch Data Flow
```
┌──────────────┐     Bluetooth/Wi-Fi     ┌──────────────┐
│  Phone App   │◄──────────────────────►│  Watch App   │
│              │                         │              │
│ Capability:  │   MessageClient:        │ Capability:  │
│ recall_      │   /recall/refresh ────► │ recall_      │
│ phone_app    │                         │ wear_app     │
│              │   DataClient:           │              │
│              │◄── /recall/deck_summary │              │
└──────────────┘                         └──────────────┘
```

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `wear/.../datalayer/WearDataLayerService.kt` | **NEW** — Wraps DataClient, MessageClient, CapabilityClient |
| `wear/.../datalayer/DataLayerListenerService.kt` | **NEW** — Receives data changes and messages |
| `wear/src/main/res/values/wear_capabilities.xml` | **NEW** — Declares "recall_wear_app" capability |
| `wear/src/main/AndroidManifest.xml` | **MODIFIED** — Registers DataLayerListenerService |
| `wear/.../WearApplication.kt` | **MODIFIED** — Adds wearModule with WearDataLayerService to Koin |
| `wear/.../presentation/WearAppContent.kt` | **MODIFIED** — Adds "Sync with Phone" Chip |

## 🎓 Learning Objectives
* Understanding the three Wearable Data Layer APIs: DataClient, MessageClient, CapabilityClient
* Implementing a WearableListenerService for background event handling
* Using `kotlinx.coroutines.tasks.await()` to convert Google Play Services Tasks to suspend functions
* Registering services with proper intent filters in AndroidManifest.xml
* Understanding when to use Data Layer API vs. shared code (KMP)

## 🧪 Verification
1. **Setup:** Create two emulators — one phone and one Wear OS watch. Pair them via the Wear OS companion app
2. **Sync Button:** Run the watch app, scroll to the bottom, tap "Sync with Phone" — you should see "Syncing..." then either "Synced!" or "No phone found"
3. **Logcat:** Filter by tag `DataLayerListener` — you should see "Received deck summary: [name], cards: [count]" when data is synced
4. **Capability:** The capability "recall_wear_app" should be discoverable from the phone side

## ⚠️ Limitations
* This demo only implements the **watch side**. A full implementation would also have a phone-side listener service
* Without a paired phone app advertising "recall_phone_app", `findPhoneNode()` will return null
* The Data Layer requires Google Play Services — it won't work on devices without GMS
