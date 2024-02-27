package com.example.fiszki.ui.flashcard

import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Round

data class FlashcardUiState(
    val deck: Deck? = null,
    val roundCount: Int = 1,
    val roundList: MutableList<Round> = mutableListOf(),
    val isDeckEnd: Boolean = false,
    val isDeckCompleted: Boolean = false,
    val deckEndText: String = "Koniec",
    val deckEndButtonText: String = "Zakończ",
    val flipFlashcardButtonText: String = "Pokaż odpowiedź",
    val summaryText: String = "",

    val initialFlashcardListSize: Int = 0,
    val totalAnswerCount: Int = 0,
    val totalCorrectAnswerCount: Int = 0,
    val totalWrongAnswerCount: Int = 0,
    val totalAnswerProgress: Float = 0f,
    val totalWrongAnswerProgress: Float = 0f,

    val currentFlashcardListSize: Int = 0,
    val currentFlashcardList: MutableList<Flashcard> = mutableListOf(),
    val currentFlashcard: Flashcard = Flashcard(),
    val currentAnswerCount: Int = 0,
    val currentWrongAnswerCount: Int = 0,
    val currentAnswerProgress: Float = 0f,
    val currentWrongAnswerProgress: Float = 0f,
    val currentFlashcardIndex: Int = 0,
    val currentFlashcardText: String = "",
    val isCurrentFlashcardFlipped: Boolean = false,
    val isCurrentAnswerRevealed: Boolean = false,
)