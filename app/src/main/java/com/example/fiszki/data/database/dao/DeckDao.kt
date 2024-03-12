package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.fiszki.data.database.entity.Deck
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Query("SELECT * FROM deck")
    fun getAll(): List<Deck>

    @Query("SELECT * FROM deck")
    fun getAllDecks(): Flow<MutableList<Deck>>

    @Query("SELECT * FROM deck WHERE id = :id LIMIT 1")
    fun getDeckById(id: Long): Deck?

    @Insert
    suspend fun insert(deck: Deck)

    @Insert
    fun insertStatic(deck: Deck)

    @Insert
    fun insertStaticWithId(deck: Deck): Long

    @Insert
    fun insertWithId(deck: Deck): Long

    @Delete
    suspend fun delete(deck: Deck)
}