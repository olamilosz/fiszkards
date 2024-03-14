package com.example.fiszki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {
    val allDecksLiveData = repository.allDecks.asLiveData()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onAddDeckButtonClick() {
        _uiState.update { currentState->
            currentState.copy(
                isNewDeckDialogVisible = true
            )
        }
    }

    fun onAddDeckButtonDialogConfirm(newDeckName: String) {
        _uiState.update { currentState->
            currentState.copy(
                isNewDeckDialogVisible = false
            )
        }
        viewModelScope.launch {
            repository.insertDeck(Deck(0, newDeckName, true))
        }
    }

    fun onAddDeckButtonDialogDismiss() {
        _uiState.update { currentState->
            currentState.copy(
                isNewDeckDialogVisible = false
            )
        }
    }

    fun setAllFlashcardCorrectAnswerNull() {
        val allFlashcards = repository.allFlashcards

        for (flashcard in allFlashcards) {
            flashcard.correctAnswer = null

            viewModelScope.launch {
                repository.updateFlashcard(flashcard)
            }
        }
    }

    fun deleteData() {
        for (deck in repository.allDecksList) {
            viewModelScope.launch { repository.deleteDeck(deck) }
        }
        for (flashcard in repository.allFlashcards) {
            viewModelScope.launch { repository.deleteFlashcard(flashcard) }
        }
    }

    fun createData() {
        val id = repository.insertDeckStatic(Deck(0,
            "Jeszcze nowy, dłuższy pusty zestaw fiszek numer", false))
        repository.insertFlashcardStatic(Flashcard(0, id, "książka", "book", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "długopis", "pen", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "ręka", "hand", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "krem", "cream", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "komputer", "computer", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "lustro", "mirror", null))
        repository.insertFlashcardStatic(Flashcard(0, id, "kubek", "mug", null))
    }

    class Factory: ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
            return HomeViewModel(
                repository = (application as FlashcardApp).repository
            ) as T
        }
    }
}