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
    val allFlashcards: List<Flashcard> = flashcardDao.getAll()
    val allDecksList: List<Deck> = deckDao.getAll()


    fun getFlashcardsByDeckIdFlow(deckId: Long): Flow<MutableList<Flashcard>> {
        return flashcardDao.getFlashcardsByDeckIdFlow(deckId)
    }

    fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard> {
        return flashcardDao.getFlashcardsByDeckId(deckId)
    }

    fun getDeckById(deckId: Long): Deck? {
        return deckDao.getDeckById(deckId)
    }

    suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    fun insertDeckStatic(deck: Deck) {
        deckDao.insertStatic(deck)
    }

    fun insertDeckWithId(deck: Deck): Long {
        return deckDao.insertStaticWithId(deck)
    }

    fun insertFlashcardStatic(flashcard: Flashcard) {
        flashcardDao.insertStatic(flashcard)
    }

    suspend fun insertDeck(deck: Deck) {
        deckDao.insert(deck)
    }

    suspend fun deleteDeck(deck: Deck) {
        deckDao.delete(deck)
    }

    suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.delete(flashcard)
    }

    suspend fun insertFlashcard(flashcard: Flashcard) {
        flashcardDao.insert(flashcard)
    }
}