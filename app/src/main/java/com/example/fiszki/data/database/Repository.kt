package com.example.fiszki.data.database

import androidx.room.Delete
import com.example.fiszki.data.database.dao.DeckDao
import com.example.fiszki.data.database.dao.FlashcardDao
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(
    private val deckDao: DeckDao,
    private val flashcardDao: FlashcardDao
) : RepositoryInterface {
    override val allDecks: Flow<MutableList<Deck>> = deckDao.getAllDecks()
    override val allFlashcards: List<Flashcard> = flashcardDao.getAll()
    override val allDecksList: List<Deck> = deckDao.getAll()

    override fun getFlashcardsByDeckIdFlow(deckId: Long): Flow<MutableList<Flashcard>> {
        return flashcardDao.getFlashcardsByDeckIdFlow(deckId)
    }

    override fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard> {
        return flashcardDao.getFlashcardsByDeckId(deckId)
    }

    override fun getDeckById(deckId: Long): Deck? {
        return deckDao.getDeckById(deckId)
    }

    override suspend fun updateFlashcard(flashcard: Flashcard) {
        flashcardDao.update(flashcard)
    }

    override suspend fun updateDeck(deck: Deck) {
        deckDao.update(deck)
    }

    override fun insertDeckStatic(deck: Deck): Long {
        return deckDao.insertStatic(deck)
    }

    override suspend fun insertDeckWithId(deck: Deck): Long {
        return deckDao.insertWithIdAsync(deck)
    }

    override fun insertFlashcardStatic(flashcard: Flashcard) {
        flashcardDao.insertStatic(flashcard)
    }

    override suspend fun insertDeck(deck: Deck) {
        deckDao.insert(deck)
    }

    override suspend fun deleteDeck(deck: Deck) {
        deckDao.delete(deck)
    }

    override suspend fun deleteFlashcard(flashcard: Flashcard) {
        flashcardDao.delete(flashcard)
    }

    suspend fun insertFlashcard(flashcard: Flashcard) {
        flashcardDao.insert(flashcard)
    }
}