package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fiszki.data.database.entity.DeckFlashcard

@Dao
interface DeckFlashcardDao {
    @Query("SELECT * FROM deck_flashcard")
    fun getAll(): List<DeckFlashcard>

    @Query("SELECT * FROM deck_flashcard WHERE deckId = :deckId")
    fun getFlashcardsByDeckId(deckId: Long?): List<DeckFlashcard>

    @Insert
    fun insert(deckFlashcard: DeckFlashcard)
}