package com.example.fiszki.data.database

import androidx.room.Delete
import com.example.fiszki.data.database.dao.DeckDao
import com.example.fiszki.data.database.dao.FlashcardDao
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.flow.Flow

class Repository(
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) {
    val allDecks: Flow<MutableList<Deck>> = deckDao.getAllDecks()

    fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard> {
        return flashcardDao.getFlashcardsByDeckId(deckId)
    }

    suspend fun insertDeck(deck: Deck) {
        deckDao.insert(deck)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckDao.delete(deck)
    }

    suspend fun insertFlashcard(flashcard: Flashcard) {
        flashcardDao.insert(flashcard)
    }
}