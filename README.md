# 💾 Exercise 8a: Database Foundation (Room KMP)
**Module:** Persistence & Performance
**Prerequisite:** Completed Module 7b
**Estimated Time:** 30 Minutes

### **🎓 Objective**
In this exercise, you will lay the foundation for "Offline Support" by implementing a local SQLite database using **Room KMP**. You will define the data structure (Entities) and the methods to read/write it (DAO), running on both Android and iOS.

### **⚠️ The "Pre-Configured" Setup**
Setting up KSP and Room Gradle plugins is complex and prone to version conflicts. We have **pre-configured** the `build.gradle.kts` and `libs.versions.toml` files for you.
* **Your Job:** Focus on the Kotlin code—defining Tables and Queries.

### **✅ Task 1: Define the Tables (Entities)**
We need to mirror our domain models (`Deck`, `Card`) into database tables.
1.  **Open** `shared/src/commonMain/kotlin/com/recall/app/data/local/entity/DeckEntity.kt`.
    * Annotate the class with `@Entity`.
    * Add `@PrimaryKey val id: String`.
    * Add `val title: String`.
2.  **Open** `CardEntity.kt`.
    * Annotate with `@Entity(foreignKeys = [...])`.
    * **Constraint:** Link `deckId` to `DeckEntity` so deleting a deck deletes its cards (`CASCADE`).

### **✅ Task 2: The Data Access Object (DAO)**
The DAO is where you write SQL (without writing much SQL).
1.  **Open** `shared/src/commonMain/kotlin/com/recall/app/data/local/dao/RecallDao.kt`.
2.  **Annotate** the interface with `@Dao`.
3.  **Implement Methods:**
    * `getDecks()`: Returns `Flow<List<DeckEntity>>`.
    * `insertDeck(...)`: A `suspend` function using `@Insert(onConflict = OnConflictStrategy.REPLACE)`.
    * `insertCard(...)`: Similar to deck.
4.  **🕵️ intentional Flaw (For Future Lesson):**
    * Add a method `getCardsForDeck(deckId: String): List<CardEntity>`.
    * *Why?* We will use this later to write a "bad" query and catch it with the Profiler.

### **✅ Task 3: The Database Class**
Connect the Entities and DAO.
1.  **Open** `shared/src/commonMain/kotlin/com/recall/app/data/local/RecallDatabase.kt`.
2.  **Annotate** the abstract class with `@Database`.
    * Include `DeckEntity` and `CardEntity` in the `entities` array.
    * Set `version = 1`.
3.  **Abstract Function:** Define `abstract fun recallDao(): RecallDao`.

### **🚀 Definition of Done**
* [ ] The project compiles without KSP errors.
* [ ] You have `DeckEntity` and `CardEntity` defined with Primary Keys.
* [ ] `RecallDao` has methods to Insert and Query data.
* [ ] `RecallDatabase` connects the entities and exposes the DAO.
* *Note:* The app won't *use* the DB yet (that's Part 8b), but the foundation must build successfully.
