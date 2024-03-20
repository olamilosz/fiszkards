package com.example.fiszki.ui.flashcard

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.fiszki.FlashcardApp
import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Round
import com.example.fiszki.ui.konfetti.Presets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party

class FlashcardViewModel(
    private val repository: Repository,
    private val deckId: Long?,
    private val resetProgress: Boolean?
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    fun explode() {
        _uiState.update { currentState ->
            currentState.copy(
                konfettiState = State.Started(Presets.explode())
            )
        }
    }

    sealed class State {
        class Started(val party: List<Party>) : State()
        object Idle : State()
    }

    init {
        startFirstRound()
    }

    private fun startFirstRound() {
        val deck = getDeck()

        if (deck != null && resetProgress != null) {
            val flashcardList = getFirstFlashcardList(deck, resetProgress)
            updateFlashcardList(flashcardList)

            _uiState.update { currentState ->
                currentState.copy(
                    roundCount = 1,
                    roundList = addNewRoundToList(1, currentState.roundList)
                )
            }
        }
    }

    fun openSettingsDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showChooseModeDialog = true
            )
        }
    }

    fun closeSettingsDialog() {
        _uiState.update { currentState ->
            currentState.copy(
                showChooseModeDialog = false
            )
        }
    }

    fun closeSettingsDialogAndUpdate(questionFirstMode: Boolean) {
        Log.d("", "questionFirstMode: $questionFirstMode")

        if (questionFirstMode != _uiState.value.questionFirstMode) {
            Log.d("", "nowy mode: $questionFirstMode stary: ${_uiState.value.questionFirstMode}")
            _uiState.update { currentState ->
                currentState.copy(
                    questionFirstMode = questionFirstMode,
                    //isCurrentFlashcardFlipped = false,
                    currentFlashcardText = if (questionFirstMode) currentState.currentFlashcard.question
                    else currentState.currentFlashcard.answer,
                    flipFlashcardButtonText = "Odwróć",
                    isCurrentAnswerRevealed = false
                )
            }

            var tempFrontText = ""
            var tempBackText = ""

            if (_uiState.value.isCurrentFlashcardFlipped) {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                }
            } else {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    currentFrontText = tempFrontText,
                    currentBackText = tempBackText
                )
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                showChooseModeDialog = false
            )
        }
    }

    fun swapFlashcardQuestionAndAnswer() {
        _uiState.update { currentState ->
            val swappedFlashcard = currentState.currentFlashcard.copy(
                answer = currentState.currentFlashcard.question,
                question = currentState.currentFlashcard.answer
            )

            currentState.copy(
                currentFlashcard = swappedFlashcard
            )
        }
    }

    private fun addNewRoundToList(number: Int, currentList: MutableList<Round>): MutableList<Round> {
        currentList.add(Round(number, 0, 0))
        return currentList
    }

    private fun updateFlashcard(flashcard: Flashcard) = viewModelScope.launch {
        repository.updateFlashcard(flashcard)
    }

    private fun updateFlashcardList(flashcardList: List<Flashcard>) {
        for (flashcard in flashcardList) {
            updateFlashcard(flashcard)
        }
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
                    isCurrentAnswerRevealed = false,
                    //isCurrentFlashcardFlipped = false
                )
            }

            //jeśli to ostatnia fiszka
            if (uiStateValue.currentFlashcardIndex == uiStateValue.currentFlashcardListSize - 1) {
                Log.d("correct button clicked", "OSTATNIA FISZKA")
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
            }

            var tempFrontText = ""
            var tempBackText = ""

            if (_uiState.value.isCurrentFlashcardFlipped) {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                }
            } else {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    //currentFlashcard = newFlashcard
                    currentFrontText = tempFrontText,
                    currentBackText = tempBackText
                    /*currentFlashcardText =
                    if (currentState.isCurrentFlashcardFlipped) {
                        if (currentState.questionFirstMode) {
                            currentState.currentFlashcard.question
                        } else {
                            currentState.currentFlashcard.answer
                        }
                    } else {
                        if (currentState.questionFirstMode) {
                            currentState.currentFlashcard.answer
                        } else {
                            currentState.currentFlashcard.question
                        }
                    }*/
                )
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
                    isDeckCompleted = true,
                    deckEndButtonText = "Zakończ",
                    deckEndTitle = "Gratulacje!"
                )
            }
        } else {
            val deckEndText = if (_uiState.value.currentAnswerCount == _uiState.value.currentWrongAnswerCount) {
                "Nie poddawaj się!"
            } else {
                "Tak trzymaj!"
            }
            _uiState.update { currentState ->
                currentState.copy(
                    deckEndTitle = deckEndText,
                    deckEndButtonText = "Rozpocznij kolejną rundę"
                )
            }
        }
    }

    fun onDeckEndButtonClicked() {
        //jesli to koniec talii, trzeba wtedy
        nextRound()
    }

    private fun nextRound() {
        val currentFlashcardList = _uiState.value.currentFlashcardList
        val nextFlashcardList = currentFlashcardList.filter {
            it.correctAnswer == false
        }.toMutableList()

        if (nextFlashcardList.isEmpty()) {
            _uiState.update { currentState ->
                currentState.copy(
                    isDeckCompleted = true,
                    isDeckEnd = true,
                    deckEndButtonText = "Zakończ",
                    deckEndTitle = "Gratulacje!"
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
                    currentFlashcardText =
                        if (currentState.questionFirstMode) nextFlashcardList.first().question
                        else nextFlashcardList.first().answer,
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
            var tempFrontText = ""
            var tempBackText = ""

            if (_uiState.value.isCurrentFlashcardFlipped) {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                }
            } else {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    currentFrontText = tempFrontText,
                    currentBackText = tempBackText
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
                    //isCurrentFlashcardFlipped = false
                )
            }

            if (uiStateValue.currentFlashcardIndex == uiStateValue.currentFlashcardListSize - 1) {
                Log.d("wrong button clicked", "OSTATNIA FISZKA")
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
            }

            var tempFrontText = ""
            var tempBackText = ""

            if (_uiState.value.isCurrentFlashcardFlipped) {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                }
            } else {
                if (_uiState.value.questionFirstMode) {
                    tempFrontText = _uiState.value.currentFlashcard.question
                    tempBackText = _uiState.value.currentFlashcard.answer
                } else {
                    tempFrontText = _uiState.value.currentFlashcard.answer
                    tempBackText = _uiState.value.currentFlashcard.question
                }
            }

            _uiState.update { currentState ->
                currentState.copy(
                    //currentFlashcard = newFlashcard
                    currentFrontText = tempFrontText,
                    currentBackText = tempBackText
                    /*currentFlashcardText =
                    if (currentState.isCurrentFlashcardFlipped) {
                        if (currentState.questionFirstMode) {
                            currentState.currentFlashcard.question
                        } else {
                            currentState.currentFlashcard.answer
                        }
                    } else {
                        if (currentState.questionFirstMode) {
                            currentState.currentFlashcard.answer
                        } else {
                            currentState.currentFlashcard.question
                        }
                    }*/
                )
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
                    if (currentState.questionFirstMode) currentState.currentFlashcard.answer
                    else currentState.currentFlashcard.question
                } else {
                    if (currentState.questionFirstMode) currentState.currentFlashcard.question
                    else currentState.currentFlashcard.answer
                },
                flipFlashcardButtonText = "Odwróć"
            )
        }
    }

    private fun getFirstFlashcardList(deck: Deck, resetProgress: Boolean): MutableList<Flashcard> {
        var flashcardList = repository.getFlashcardsByDeckId(deck.id)

        if (resetProgress) {
            for (flashcard in flashcardList) {
                flashcard.correctAnswer = null
            }
        }

        flashcardList = flashcardList.filter { flashcard ->
            flashcard.correctAnswer == false || flashcard.correctAnswer == null
        }.toMutableList()

        if (flashcardList.isNotEmpty()) {
            _uiState.update { currentState->
                currentState.copy(
                    currentFlashcardList = flashcardList,
                    currentFlashcardListSize = flashcardList.size,
                    currentFlashcard = flashcardList.first(),
                    currentFlashcardText = flashcardList.first().question,
                    initialFlashcardListSize = flashcardList.size,
                    currentFrontText = flashcardList.first().question,
                    currentBackText = flashcardList.first().answer
                )
            }
            return flashcardList
        }
        return mutableListOf()
    }

    fun getFlashcardsLeftToCompleteCount(): Int {
        return _uiState.value.initialFlashcardListSize - _uiState.value.totalCorrectAnswerCount
    }

    fun getFlashcardsLeftToCompleteText(): String {
        val count = _uiState.value.initialFlashcardListSize - _uiState.value.totalCorrectAnswerCount

        if (count == 1) {
            return "Została Ci jedna fiszka do ukończenia talii!"
        } else {
            val range = 2..4
            val lastDigit = count % 10

            if (range.contains(lastDigit)) {
                return "Zostały Ci $count fiszki do ukończenia talii!"
            } else {
                return "Zostało Ci $count fiszek do ukończenia talii!"
            }
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

    class Factory(
        private val deckId: Long?,
        private val resetProgress: Boolean?
    ): ViewModelProvider.Factory  {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application =
                checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
            return FlashcardViewModel(
                repository = (application as FlashcardApp).repository, deckId, resetProgress
            ) as T
        }
    }
}