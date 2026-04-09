package com.recall.app.data.local

import androidx.room.RoomDatabase
import com.recall.app.data.local.dao.RecallDao
import com.recall.app.data.local.entity.CardEntity
import com.recall.app.data.local.entity.DeckEntity

// TODO 4: Import Room database annotation
//  Hint: import androidx.room.Database

// TODO 4: Annotate this class with @Database
//  - Set entities parameter to array of: DeckEntity::class, CardEntity::class
//  - Set version to 1 (this is the first version of the schema)
//  - Set exportSchema to false (avoids schema export warnings in KMP)
//  Hint: Use the following syntax:
//    @Database(
//        entities = [DeckEntity::class, CardEntity::class],
//        version = 1,
//        exportSchema = false
//    )
abstract class RecallDatabase : RoomDatabase() {

    // TODO 4: Create an abstract function that returns RecallDao
    //  - Function name: recallDao()
    //  - Return type: RecallDao
    //  - Room will generate the implementation automatically
    //  Hint: Method signature:
    //    abstract fun recallDao(): RecallDao
}
