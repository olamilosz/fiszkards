package com.example.fiszki.ui.flashcard

import com.example.fiszki.data.database.entity.Flashcard

data class FlashcardUiState(
    val deckName: String = "",
    val flashcardList: List<Flashcard> = mutableListOf(),
    val flashcardListSize: Int = 0,
    val answerCount: Int = 0,
    val wrongAnswerCount: Int = 0,
    val isDeckEnd: Boolean = false,
    val currentFlashcard: Flashcard = Flashcard(),
    val currentFlashcardIndex: Int = 0,
    val currentFlashcardText: String = "",
    val isCurrentFlashcardFlipped: Boolean = false,
    val isCurrentFlashcardRevealed: Boolean = false
)