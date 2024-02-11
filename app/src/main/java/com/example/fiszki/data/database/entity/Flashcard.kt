package com.example.fiszki.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcard")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val question: String,
    val answer: String,
    val correctAnswer: Boolean?
)