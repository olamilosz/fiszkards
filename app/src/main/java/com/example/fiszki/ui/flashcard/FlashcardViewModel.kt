package com.example.fiszki.ui.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlashcardViewModel(
    private val repository: Repository,
    private val deckId: Long?
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    init {
        startFirstRound()
    }

    private fun startFirstRound() {
        val deck = getDeck()

        if (deck != null) {
            getFlashcardList(deck)

            _uiState.update { currentState ->
                currentState.copy(
                    roundCount = 1,
                    roundList = addNewRoundToList(1, currentState.roundList)
                )
            }
        }
    }

    private fun addNewRoundToList(number: Int, currentList: MutableList<Round>): MutableList<Round> {
        currentList.add(Round(number, null, null))
        return currentList
    }

    private fun updateFlashcard(flashcard: Flashcard) = viewModelScope.launch {
        repository.updateFlashcard(flashcard)
    }

    fun getSummaryResultText(): String {
        return "Ogólny wynik: ${_uiState.value.totalCorrectAnswerCount} / ${_uiState.value.initialFlashcardListSize}"
    }

    fun onCorrectAnswerButtonClicked() {
        val uiStateValue = _uiState.value

        if (uiStateValue.currentFlashcardIndex < uiStateValue.currentFlashcardListSize) {
            val currentFlashcard = _uiState.value.currentFlashcard

            //jeśli obecna fiszka wcześniej miała złą odpowiedź, trzeba odjąc liczbę złych
            // odpowiedzi total oprocz zrobienia updatu
            if (currentFlashcard.correctAnswer == false) {
                _uiState.update { currentState ->
                    currentState.copy(
                        totalWrongAnswerCount = currentState.totalWrongAnswerCount - 1,
                        totalWrongAnswerProgress = (currentState.totalWrongAnswerCount - 1) /
                                (currentState.initialFlashcardListSize).toFloat()
                    )
                }
            }

            currentFlashcard.correctAnswer = true
            updateFlashcard(currentFlashcard)

            _uiState.update { currentState ->
                currentState.copy(
                    currentAnswerCount = currentState.currentAnswerCount + 1,
                    currentAnswerProgress = (currentState.currentAnswerCount + 1) /
                            (currentState.currentFlashcardListSize).toFloat(),
                    totalAnswerCount = currentState.totalAnswerCount + 1,
                    totalCorrectAnswerCount = currentState.totalCorrectAnswerCount + 1,
                    totalAnswerProgress = (currentState.totalAnswerCount + 1) /
                            (currentState.initialFlashcardListSize).toFloat(),
                    isCurrentAnswerRevealed = false
                )
            }

            //jeśli to ostatnia fiszka
            if (uiStateValue.currentFlashcardIndex == uiStateValue.currentFlashcardListSize - 1) {
                updateRound()
                _uiState.update { currentState ->
                    currentState.copy(
                        isDeckEnd = true
                    )
                }
                return

            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentFlashcardIndex = currentState.currentFlashcardIndex + 1,
                        currentFlashcard = currentState
                            .currentFlashcardList[currentState.currentFlashcardIndex + 1]
                    )
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        currentFlashcardText = currentState.currentFlashcard.question
                    )
                }
            }
        }
    }

    private fun updateRound() {
        val uiState = _uiState.value
        val currentCorrectAnswerCount = uiState.currentAnswerCount - uiState.currentWrongAnswerCount

        uiState.roundList.last().roundListSize = uiState.currentFlashcardListSize
        uiState.roundList.last().correctAnswerCount = currentCorrectAnswerCount

        _uiState.update { currentState ->
            currentState.copy(
                roundList = uiState.roundList
            )
        }

        //dodatkowo sprawdzamy czy skończyła się talia
        val currentFlashcardList = _uiState.value.currentFlashcardList
        val nextFlashcardList = currentFlashcardList.filter {
            it.correctAnswer == false
        }.toMutableList()

        if (nextFlashcardList.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isDeckCompleted = true
                )
            }
        }
    }

    fun onDeckEndButtonClicked() {
        //jesli to koniec talii, trzeba wtedy
        nextRound()
    }

    private fun nextRound() {
        //jeśli są błędne odpowiedzi to trzeba zrobić nową rundę i zrobić update nowej talii
        val currentFlashcardList = _uiState.value.currentFlashcardList
        val nextFlashcardList = currentFlashcardList.filter {
            it.correctAnswer == false
        }.toMutableList()

        if (nextFlashcardList.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isDeckCompleted = true,
                    isDeckEnd = true
                )
            }

        } else {
            val newRoundNumber = _uiState.value.roundList.last().roundNumber + 1
            val newRound = addNewRoundToList(newRoundNumber, _uiState.value.roundList)

            _uiState.update { currentState ->
                currentState.copy(
                    currentFlashcardList = nextFlashcardList,
                    currentFlashcardListSize = nextFlashcardList.size,
                    currentFlashcard = nextFlashcardList.first(),
                    currentFlashcardIndex = 0,
                    currentFlashcardText = nextFlashcardList.first().question,
                    currentWrongAnswerCount = 0,
                    currentWrongAnswerProgress = 0f,
                    currentAnswerCount = 0,
                    currentAnswerProgress = 0f,
                    isCurrentAnswerRevealed = false,
                    isCurrentFlashcardFlipped = false,
                    isDeckEnd = false,
                    roundList = newRound,
                    roundCount = newRoundNumber
                )
            }
        }
    }

    fun onWrongAnswerButtonClicked() {
        val uiStateValue = _uiState.value

        if (uiStateValue.currentFlashcardIndex < uiStateValue.currentFlashcardListSize) {
            val currentFlashcard = _uiState.value.currentFlashcard
            currentFlashcard.correctAnswer = false
            updateFlashcard(currentFlashcard)

            _uiState.update { currentState ->
                currentState.copy(
                    currentAnswerCount = currentState.currentAnswerCount + 1,
                    currentAnswerProgress = (currentState.currentAnswerCount + 1) /
                            (currentState.currentFlashcardListSize).toFloat(),
                    totalAnswerCount = currentState.totalAnswerCount + 1,
                    totalAnswerProgress = (currentState.totalAnswerCount + 1) /
                            (currentState.initialFlashcardListSize).toFloat(),
                    currentWrongAnswerCount = currentState.currentWrongAnswerCount + 1,
                    currentWrongAnswerProgress = (currentState.currentWrongAnswerCount + 1) /
                            (currentState.currentFlashcardListSize).toFloat(),
                    totalWrongAnswerCount = currentState.totalWrongAnswerCount + 1,
                    totalWrongAnswerProgress = (currentState.totalWrongAnswerCount + 1) /
                            (currentState.initialFlashcardListSize).toFloat(),
                    isCurrentAnswerRevealed = false
                )
            }

            if (uiStateValue.currentFlashcardIndex == uiStateValue.currentFlashcardListSize - 1) {
                _uiState.update { currentState ->
                    updateRound()
                    currentState.copy(
                        isDeckEnd = true
                    )
                }

                return

            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        currentFlashcardIndex = currentState.currentFlashcardIndex + 1,
                        currentFlashcard = currentState
                            .currentFlashcardList[currentState.currentFlashcardIndex + 1]
                    )
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        currentFlashcardText = currentState.currentFlashcard.question
                    )
                }
            }
        }
    }

    fun onFlipFlashcardButtonClicked() {
        _uiState.update { currentState ->
            currentState.copy(
                isCurrentAnswerRevealed = true,
                isCurrentFlashcardFlipped = !currentState.isCurrentFlashcardFlipped
            )
        }
        _uiState.update { currentState ->
            currentState.copy(
                currentFlashcardText = if (currentState.isCurrentFlashcardFlipped) {
                    currentState.currentFlashcard.answer
                } else {
                    currentState.currentFlashcard.question
                },
                flipFlashcardButtonText = if (currentState.isCurrentFlashcardFlipped) {
                    "Pokaż pytanie"
                } else {
                    "Pokaż odpowiedź"
                }
            )
        }
    }

    private fun getFlashcardList(deck: Deck): MutableList<Flashcard> {
        val flashcardList = repository.getFlashcardsByDeckId(deck.id)

        if (flashcardList.isNotEmpty()) {
            _uiState.update { currentState->
                currentState.copy(
                    currentFlashcardList = flashcardList,
                    currentFlashcardListSize = flashcardList.size,
                    currentFlashcard = flashcardList.first(),
                    currentFlashcardText = flashcardList.first().question,
                    initialFlashcardListSize = flashcardList.size
                )
            }
            return flashcardList
        }
        return mutableListOf()
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

    class Factory(private val deckId: Long?): ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
            return FlashcardViewModel(
                repository = (application as FlashcardApp).repository, deckId
            ) as T
        }
    }
}