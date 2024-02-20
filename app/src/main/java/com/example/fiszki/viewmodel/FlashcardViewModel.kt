package com.example.fiszki.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.ui.flashcard.FlashcardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: Repository
) : ViewModel() {
    private val uiState = MutableStateFlow(FlashcardUiState())

    fun getFlashcardUiStateByDeckId(deckId: Long?): StateFlow<FlashcardUiState> {
        return uiState.asStateFlow()
    }

    fun getFlashcardsByDeckId(deckId: Long): MutableList<Flashcard> {
         return repository.getFlashcardsByDeckId(deckId)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return FlashcardViewModel(
                    repository = (application as FlashcardApp).repository
                ) as T
            }
        }
    }
}