package com.example.fiszki.ui.deck

import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard

data class DeckUiState (
    val deck: Deck? = null,
    val deckName: String = "",
    val flashcardList: MutableList<Flashcard> = mutableListOf(),
    val flashcardListSize: Int = 0,
    val answerCount: Int = 0,
    val answerProgress: Float = 0f,
    val wrongAnswerCount: Int = 0,
    val wrongAnswerProgress: Float = 0f,
    val showFlashcardList: Boolean = false
)