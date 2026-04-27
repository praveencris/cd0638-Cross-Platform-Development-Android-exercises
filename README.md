# 🧠 Exercise 8b: Offline Intelligence
**Branch:** `lesson-8b-db-foundation-setup`
**Module:** Persistence & Performance
**Prerequisite:** Completed Module 8a (Database Defined)
**Estimated Time:** 45 Minutes

## Branch Context

This is a student exercise branch. Some code is intentionally incomplete or simplified so you can complete the lesson tasks in order.

Use the matching sol-* branch to review the completed solution, and the matching prod-sol-* branch when you want to see production hardening for the same lesson.


### **🎓 Objective**
In this exercise, you will implement the **Offline-First** pattern. You will "wire up" the database you created in Part 8a and rewire the `RecallRepository` so that the app *always* shows data from the local database (instant load), and only uses the network to update that database in the background.

### **✅ Task 1: The "Wiring" (Dependency Injection)**
We defined the Database class in 8a, but our app doesn't know how to create it or provide it to the Repository. We need to register it in **Koin**.

1.  **Open** `composeApp/src/androidMain/kotlin/com/recall/app/di/AndroidModule.kt`.
    * **Action:** Register the `DatabaseFactory` as a `single`.
    * *Hint:* `single { DatabaseFactory(androidContext()) }`

2.  **Open** `shared/src/commonMain/kotlin/com/recall/app/di/Koin.kt`.
    * **Action:** Register the `RecallDatabase` as a `single`.
    * *Hint:* Use `get<DatabaseFactory>().create()` to build it.
    * **Action:** Register the `RecallDao` as a `single`.
    * *Hint:* Call `.recallDao()` on the database instance.

### **✅ Task 2: The Repository Switch**
Currently, `RecallRepository` talks directly to the API. Let's change that to use the DAO.

1.  **Open** `shared/src/commonMain/kotlin/com/recall/app/data/RecallRepository.kt`.
2.  **Constructor:** Add `private val recallDao: RecallDao` to the constructor arguments.
3.  **Refactor `getDecks()`:**
    * **Old:** `flow { emit(api.getDecks()) }`
    * **New:** `recallDao.getDecks()`
    * **Mapping:** You will need to map the `List<DeckEntity>` from the DB to the domain `List<Deck>`.
    * *Hint:* Use the `.map { ... }` operator on the Flow.

### **✅ Task 3: The "Refresh" Logic**
Since `getDecks()` now reads from the disk, we need a way to put data *onto* the disk.

1.  **Refactor `refreshDecks()`:**
    * Make it `suspend`.
    * **Step 1:** Call `api.getDecks()` to get fresh data.
    * **Step 2:** Map the domain models to `DeckEntity` and `CardEntity`.
    * **Step 3:** Call `recallDao.insertDecks(...)` and `insertCards(...)`.
    * **Benefit:** When this saves to disk, the Flow in Task 2 will *automatically* emit the new data to the UI!

### **✅ Task 4: Verify "Airplane Mode"**
1.  **Run** the app on Android.
2.  **Load** the decks once (to cache them).
3.  **Kill** the app.
4.  **Turn off WiFi/Data** (Airplane Mode).
5.  **Relaunch** the app.
6.  **Success:** The decks should appear instantly! (Loaded from Room).

### **🚀 Definition of Done**
* [ ] The app compiles (Koin wiring is correct).
* [ ] `RecallRepository` uses `recallDao` as the primary data source.
* [ ] The app displays data even when the device is offline.
* [ ] Pull-to-refresh updates the local database.
