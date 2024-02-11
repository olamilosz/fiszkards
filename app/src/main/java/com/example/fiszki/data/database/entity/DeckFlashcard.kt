package com.example.fiszki.data.database.entity

import androidx.room.Entity

@Entity(primaryKeys = ["deckId", "flashcardId"], tableName = "deck_flashcard")
data class DeckFlashcard(
    val deckId: Long,
    val flashcardId: Long
)