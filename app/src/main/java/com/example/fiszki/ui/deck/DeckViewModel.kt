package com.example.fiszki.ui.deck

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.ui.flashcard.FlashcardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckViewModel(
    private val repository: Repository,
    private val deckId: Long?
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeckUiState())
    val uiState: StateFlow<DeckUiState> = _uiState.asStateFlow()
    lateinit var flashcardListLiveData: LiveData<MutableList<Flashcard>>

    init {
        initializeDeck()
    }

    private fun initializeDeck() {
        val deck = getDeck()

        if (deck != null) {
            flashcardListLiveData = repository.getFlashcardsByDeckIdFlow(deckId!!).asLiveData()
            val flashcardList = repository.getFlashcardsByDeckId(deck.id)
            val flashcardListSize = flashcardList.size

            if (flashcardListSize > 0) {
                val answerCount = flashcardList.count { it.correctAnswer != null }
                val wrongAnswerCount = flashcardList.count { it.correctAnswer == false }
                val correctAnswerCount = flashcardList.count { it.correctAnswer == true }
                val goToFlashcardScreenButtonText = when {
                    correctAnswerCount == flashcardListSize -> "Zresetuj wynik i zacznij od nowa"
                    answerCount == 0 -> "Rozpocznij naukę"
                    answerCount < flashcardListSize -> "Kontynuuj naukę"
                    else -> "Kontynuuj naukę"
                }
                Log.d("goToFlashcardScreenButtonText", goToFlashcardScreenButtonText)

                _uiState.update { currentState->
                    currentState.copy(
                        deck = deck,
                        deckName = deck.deckName,
                        flashcardList = flashcardList,
                        flashcardListSize = flashcardList.size,
                        answerCount = answerCount,
                        correctAnswerCount = correctAnswerCount,
                        wrongAnswerCount = wrongAnswerCount,
                        answerProgress = answerCount / flashcardListSize.toFloat(),
                        wrongAnswerProgress = wrongAnswerCount / flashcardListSize.toFloat(),
                        goToFlashcardScreenButtonText = goToFlashcardScreenButtonText
                    )
                }
            }
        }
    }

    fun getResetProgressValue(): Boolean {
        val correctAnswerCount = _uiState.value.flashcardList.count { it.correctAnswer == true }
        return correctAnswerCount == _uiState.value.flashcardListSize
    }

    private fun updateFlashcard(flashcard: Flashcard) = viewModelScope.launch {
        repository.updateFlashcard(flashcard)
    }

    fun updateFlashcardList(flashcardList: MutableList<Flashcard>) {
        if (flashcardList.isNotEmpty()) {
            val answerCount = flashcardList.filter { flashcard -> flashcard.correctAnswer != null }.size
            val wrongAnswerCount = flashcardList.filter { flashcard -> flashcard.correctAnswer == false }.size
            val answerProgress = answerCount / flashcardList.size.toFloat()
            val wrongAnswerProgress = wrongAnswerCount / flashcardList.size.toFloat()
            val correctAnswerCount = flashcardList.count { it.correctAnswer == true }
            val goToFlashcardScreenButtonText = when {
                correctAnswerCount == flashcardList.size -> "Zresetuj wynik i zacznij od nowa"
                answerCount == 0 -> "Rozpocznij naukę"
                answerCount < flashcardList.size -> "Kontunuuj naukę"
                else -> "Kontunuuj naukę"
            }

            _uiState.update { currentState ->
                currentState.copy(
                    flashcardList = flashcardList,
                    flashcardListSize = flashcardList.size,
                    answerCount = flashcardList.filter { flashcard -> flashcard.correctAnswer != null }.size,
                    answerProgress = answerProgress,
                    correctAnswerCount = correctAnswerCount,
                    wrongAnswerCount = wrongAnswerCount,
                    wrongAnswerProgress = wrongAnswerProgress,
                    goToFlashcardScreenButtonText = goToFlashcardScreenButtonText
                )
            }
        }
    }

    private fun getDeck(): Deck? {
        if (deckId != null) {
            val deck = repository.getDeckById(deckId)

            _uiState.update { currentState ->
                currentState.copy(
                    deck = deck
                )
            }
            return deck
        }
        return null
    }

    fun showFlashcardListScreen() {
        _uiState.update { currentState ->
            currentState.copy(
                showFlashcardList = true
            )
        }
    }

    fun exitFlashcardListScreen() {
        _uiState.update { currentState ->
            currentState.copy(
                showFlashcardList = false
            )
        }
    }

    class Factory(private val deckId: Long?): ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
            return DeckViewModel(
                repository = (application as FlashcardApp).repository, deckId
            ) as T
        }
    }

}