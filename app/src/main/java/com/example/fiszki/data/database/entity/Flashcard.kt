package com.example.fiszki.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long = 0,
    val question: String = "",
    val answer: String = "",
    var correctAnswer: Boolean? = null
)