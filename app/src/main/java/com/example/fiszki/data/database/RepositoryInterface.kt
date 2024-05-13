package com.example.fiszki.data.database

import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    val allDecks: Flow<MutableList<Deck>>
    val allFlashcards: List<Flashcard>
    val allDecksList: List<Deck>
    fun getFlashcardsByDeckIdFlow(deckId: Long): Flow<MutableList<Flashcard>>
    fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard>
    fun getDeckById(deckId: Long): Deck?
    suspend fun updateFlashcard(flashcard: Flashcard)
    suspend fun updateDeck(deck: Deck)
    fun insertDeckStatic(deck: Deck): Long
    suspend fun insertDeckWithId(deck: Deck): Long
    fun insertFlashcardStatic(flashcard: Flashcard)
    suspend fun insertDeck(deck: Deck)
    suspend fun deleteDeck(deck: Deck)
    suspend fun deleteFlashcard(flashcard: Flashcard)
}