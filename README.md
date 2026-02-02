# 🏁 Solution 7b: Advanced Wear OS
**Branch:** `solution-module-7b`

This branch extends the basic Wear OS app with deep system integrations, transforming it from a simple "List Viewer" into a native citizen of the watch ecosystem.

## 🛠️ Implementation Summary

### 1. Health Services (Passive Data)
We implemented `HealthServicesClient` to read the user's Heart Rate.
* **Key Class:** `MeasureClient`
* **Permission:** `android.permission.BODY_SENSORS`
* **Implementation:** We use a `produceState` in Compose to subscribe to the `HEART_RATE_BPM` flow only when the UI is visible. This ensures we don't drain the battery when the user isn't looking at the screen.

### 2. Watch Face Complication
We created a `SuspendingComplicationDataSourceService`.
* **Type:** `ShortText` (Standard layout for small round slots).
* **Update Cycle:** The OS polls this service. We return a `ShortTextComplicationData` built using the `ComplicationDataSourceService` API.
* **Deep Link:** Tapping the complication fires a `PendingIntent` that launches `MainActivity`.

### 3. Tiles (ProtoLayout)
We created a `SuspendingTileService`.
* **Tech:** Tiles do **not** use Compose. They use **ProtoLayout**, a declarative schema that is serialized and sent to the OS to render.
* **Layout:** We used `PrimaryLayout` from the `protolayout-material` library to easily create a standard "Title + Chip + Button" design without manual pixel math.

## 📂 Key Files
| File Path | Description |
| :--- | :--- |
| `wear/.../complication/RecallComplicationService.kt` | Logic for the "Due Count" widget. |
| `wear/.../tile/RecallTileService.kt` | Logic for the "Quick Study" tile. |
| `wear/.../presentation/WearApp.kt` | Updated with Heart Rate display. |
| `wear/src/main/AndroidManifest.xml` | Added Services and Permissions. |

## 🧪 Verification
1.  **Health:** Run the app on an Emulator. Open "Extended Controls" (3 dots) > "Wear OS" > "Health" and change the Heart Rate. The UI should update instantly.
2.  **Complication:** Long-press the watch face > Edit > Tap a slot > Select "Recall". It should appear.
3.  **Tile:** Swipe left/right on the watch face > "Add Tile" > Select "Recall". The UI should render.
