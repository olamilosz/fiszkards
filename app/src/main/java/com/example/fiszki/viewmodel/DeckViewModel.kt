package com.example.fiszki.viewmodel

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
import kotlinx.coroutines.launch

class DeckViewModel(
    private val repository: Repository
) : ViewModel() {
    val allDecksLiveData: LiveData<MutableList<Deck>> = repository.allDecks.asLiveData()

    fun insert(deck: Deck) = viewModelScope.launch {
        repository.insertDeck(deck)
    }

    fun insertFlashcard(flashcard: Flashcard) = viewModelScope.launch {
        repository.insertFlashcard(flashcard)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object: ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return DeckViewModel((application as FlashcardApp).repository) as T
            }
        }
    }
}