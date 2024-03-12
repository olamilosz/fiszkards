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
import com.example.fiszki.ui.deck.DeckViewModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {
    val allDecksLiveData = repository.allDecks.asLiveData()

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
        for (i in 1L..4) {
            val id = repository.insertDeckWithId(Deck(0,
                "Jeszcze nowy, dłuższy pusty zestaw fiszek numer $i" +
                        "Jeszcze nowy, dłuższy pusty zestaw fiszek numer $i"))
            //repository.insertFlashcardStatic(Flashcard(0, id, "książka", "book", null))
            //repository.insertFlashcardStatic(Flashcard(0, id, "długopis", "pen", null))
            //repository.insertFlashcardStatic(Flashcard(0, id, "ręka", "hand", null))
            //repository.insertFlashcardStatic(Flashcard(0, id, "krem", "cream", null))

        }
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