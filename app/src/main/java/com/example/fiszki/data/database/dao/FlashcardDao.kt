package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard")
    fun getAll(): List<Flashcard>

    @Query("SELECT * FROM flashcard WHERE id = :id LIMIT 1")
    fun getFlashcardById(id: Long): Flashcard

    @Query("SELECT * FROM flashcard WHERE deckId = :deckId")
    fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard>

    @Update
    suspend fun update(flashcard: Flashcard)

    @Insert
    suspend fun insert(flashcard: Flashcard)

    @Insert
    fun insertWithId(flashcard: Flashcard): Long
}