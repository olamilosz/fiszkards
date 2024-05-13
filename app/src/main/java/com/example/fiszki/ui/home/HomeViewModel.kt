package com.example.fiszki.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fiszki.data.database.RepositoryInterface
import com.example.fiszki.data.database.entity.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RepositoryInterface
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
}