package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.fiszki.data.database.entity.Deck

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck")
    fun getAll(): List<Deck>

    @Query("SELECT * FROM deck WHERE id = :id LIMIT 1")
    fun getDeckById(id: Long): Deck?

    @Insert
    fun insert(flashcard: Deck)

    @Insert
    fun insertWithId(flashcard: Deck): Long
}