package com.example.fiszki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.ui.deck.DeckViewModel

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {
    val allDecksLiveData = repository.allDecks.asLiveData()

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