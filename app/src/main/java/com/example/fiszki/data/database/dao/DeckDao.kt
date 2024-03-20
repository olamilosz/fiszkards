package com.example.fiszki.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Update
    suspend fun update(deck: Deck)

    @Insert
    suspend fun insert(deck: Deck)

    @Insert
    suspend fun insertDeckWithId(deck: Deck): Long

    @Insert
    fun insertStatic(deck: Deck): Long

    @Insert
    fun insertStaticWithId(deck: Deck): Long

    @Insert
    fun insertWithId(deck: Deck): Long

    @Insert
    suspend fun insertWithIdAsync(deck: Deck): Long

    @Delete
    suspend fun delete(deck: Deck)
}