package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fiszki.data.database.entity.Flashcard

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcard")
    fun getAll(): List<Flashcard>

    @Query("SELECT * FROM flashcard WHERE id = :id LIMIT 1")
    fun getFlashcardById(id: Long?): Flashcard

    @Insert
    fun insert(flashcard: Flashcard)

    @Insert
    fun insertWithId(flashcard: Flashcard): Long
}