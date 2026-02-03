package com.recall.app.data.local.entity

// TODO 1: Import the necessary Room annotations (@Entity, @PrimaryKey)
//  Hint: import androidx.room.Entity
//  Hint: import androidx.room.PrimaryKey

// TODO 1: Annotate this class with @Entity to mark it as a database table
//  Hint: Add @Entity above the data class
data class DeckEntity(
    // TODO 1: Add @PrimaryKey annotation to mark 'id' as the primary key
    //  Hint: Add @PrimaryKey before val id: String
    val id: String,
    val title: String
)
