package com.recall.app.data.local.entity

// TODO 1: Import the necessary Room annotations
//  Hint: import androidx.room.Entity
//  Hint: import androidx.room.PrimaryKey
//  Hint: import androidx.room.ForeignKey

// TODO 1: Annotate this class with @Entity
// TODO 1: Add a foreignKeys parameter to link this table to DeckEntity
//  Hint: Use the following syntax:
//    @Entity(
//        foreignKeys = [ForeignKey(
//            entity = DeckEntity::class,
//            parentColumns = ["id"],
//            childColumns = ["deckId"],
//            onDelete = ForeignKey.CASCADE
//        )]
//    )
//  This ensures when a deck is deleted, all its cards are deleted too (CASCADE)
data class CardEntity(
    // TODO 1: Add @PrimaryKey annotation
    //  Hint: Add @PrimaryKey before val id: String
    val id: String,
    val deckId: String,  // Links to DeckEntity.id
    val front: String,
    val back: String,
    val ease: Float,
    val interval: Int,
    val imageBase64: String?
)
