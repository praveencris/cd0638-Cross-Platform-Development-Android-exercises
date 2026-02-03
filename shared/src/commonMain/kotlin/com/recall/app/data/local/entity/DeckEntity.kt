package com.recall.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeckEntity(
    @PrimaryKey val id: String,
    val title: String
)
