package com.example.fiszki.ui.flashcard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.MainActivity
import com.example.fiszki.R
import com.example.fiszki.data.database.entity.Round
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.ui.theme.montserratFontFamily

class FlashcardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.extras?.getLong("deckId")

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FlashcardScreen(deckId)
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardScreen(deckId: Long?) {
    val flashcardViewModel: FlashcardViewModel =
        viewModel(factory = FlashcardViewModel.Factory(deckId))
    val uiState by flashcardViewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (!uiState.isDeckEnd && uiState.deck != null) {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = uiState.deck!!.deckName,
                    textAlign = TextAlign.Left,
                    fontSize = 28.sp,
                    fontFamily = libreBaskervilleFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(
                            3.dp,
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = LocalColors.current.closeIconBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }

            MultipleLinearProgressIndicator(
                primaryProgress = uiState.currentWrongAnswerProgress,
                secondaryProgress = uiState.currentAnswerProgress,
                modifier = Modifier.padding(0.dp, 10.dp),
                backgroundColor = LocalColors.current.grey
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            ) {
                Flashcard(text = uiState.currentFlashcardText)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                if (uiState.isCurrentAnswerRevealed) {
                    Button(
                        onClick = {
                            flashcardViewModel.onWrongAnswerButtonClicked()
                        },
                        colors = ButtonDefaults.buttonColors(
                            LocalColors.current.wrongButton
                        ),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = "Nie wiedziałem")
                    }

                    Button(
                        onClick = {
                            flashcardViewModel.onCorrectAnswerButtonClicked()
                        },
                        colors = ButtonDefaults.buttonColors(
                            LocalColors.current.correctButton
                        ),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = "Wiedziałem")
                    }
                }
            }

            Button(
                onClick = {
                    flashcardViewModel.onFlipFlashcardButtonClicked()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(text = uiState.flipFlashcardButtonText)
            }
        }
    } else {
        if (uiState.deck != null) {
            DeckEndScreen(
                {
                    if (uiState.isDeckCompleted) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    } else {
                        flashcardViewModel.onDeckEndButtonClicked()
                    }
                },
                uiState.summaryText,
                uiState.deckEndButtonText,
                flashcardViewModel.getSummaryResultText(),
                uiState.roundList)
        } else {
            // ekran błędu
            Text(text = "Błąd")
        }
    }
}

/*@Composable
fun FlashcardScreen2(deckName: String, flashcardList: MutableList<Flashcard>) {
    var currentFlashcardList = remember { flashcardList }
    var wrongAnswerCount by remember { mutableIntStateOf(0) }
    var answerCount by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var currentFlashcard by remember { mutableStateOf(currentFlashcardList.first()) }
    var currentFlashcardIndex by remember { mutableIntStateOf(0) }
    var flashcardListSize by remember { mutableIntStateOf(currentFlashcardList.size) }
    var flashcardText by remember { mutableStateOf(currentFlashcard.question) }
    var revealed by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val openDeckEndDialog = remember { mutableStateOf(false) }
    var roundCount by remember { mutableIntStateOf(1) }

    if (!openDeckEndDialog.value) {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = deckName,
                    textAlign = TextAlign.Left,
                    fontSize = 28.sp,
                    fontFamily = libreBaskervilleFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(
                            3.dp,
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = LocalColors.current.closeIconBackground,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                )
            }

            MultipleLinearProgressIndicator(
                primaryProgress = (wrongAnswerCount) / (flashcardListSize).toFloat(),
                secondaryProgress = (answerCount) / (flashcardListSize).toFloat(),
                modifier = Modifier.padding(0.dp, 10.dp),
                backgroundColor = LocalColors.current.grey
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)

            ) {
                Flashcard(text = flashcardText)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                if (revealed) {
                    Button(
                        onClick = {
                            if (answerCount < flashcardListSize && wrongAnswerCount < flashcardListSize) {
                                currentFlashcard.correctAnswer = false
                                answerCount++
                                wrongAnswerCount++
                            }

                            Log.d("liczba odpowiedzi", "odp $answerCount ilosc $flashcardListSize")
                            if (answerCount == flashcardListSize) {
                                openDeckEndDialog.value = true
                            }

                            if (currentFlashcardIndex < flashcardListSize - 1) {
                                currentFlashcardList[currentFlashcardIndex] = currentFlashcard

                                currentFlashcardIndex++
                                currentFlashcard = currentFlashcardList[currentFlashcardIndex]
                                flashcardText = currentFlashcard.question
                                expanded = false
                                Log.d(
                                    "size",
                                    "current $currentFlashcardIndex size $flashcardListSize "
                                )
                                revealed = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            LocalColors.current.wrongButton
                        ),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = "Nie wiedziałem")
                    }

                    Button(
                        onClick = {
                            if (answerCount < flashcardListSize) {
                                answerCount++
                                currentFlashcard.correctAnswer = true
                            }

                            Log.d("liczba odpowiedzi", "odp $answerCount ilosc $flashcardListSize")

                            if (answerCount == flashcardListSize) {
                                openDeckEndDialog.value = true
                            }

                            if (currentFlashcardIndex < flashcardListSize - 1) {
                                currentFlashcardList[currentFlashcardIndex] = currentFlashcard

                                currentFlashcardIndex++
                                currentFlashcard = currentFlashcardList[currentFlashcardIndex]
                                flashcardText = currentFlashcard.question
                                expanded = false
                                Log.d(
                                    "size",
                                    "current $currentFlashcardIndex size $flashcardListSize "
                                )
                                revealed = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            LocalColors.current.correctButton
                        ),
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(text = "Wiedziałem")
                    }
                }
            }

            Button(
                onClick = {
                    expanded = !expanded
                    revealed = true

                    flashcardText = if (expanded) {
                        currentFlashcard.answer
                    } else {
                        currentFlashcard.question
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                if (expanded) {
                    Text(text = "Pokaż definicję")
                } else {
                    Text(text = "Pokaż odpowiedź")
                }
            }
        }
    } else {
        Log.d("KONIEC", "KONIEC ${openDeckEndDialog.value}")
        val correctAnswerCount = answerCount - wrongAnswerCount
        var text = ""
        text = if (correctAnswerCount != answerCount) {
            "Runda $roundCount\nWynik: $correctAnswerCount / $answerCount"
        } else {
            "KONIEC!!!\nRunda $roundCount\nWynik: $correctAnswerCount / $answerCount"
        }

        DeckEndScreen(onButtonPressed = {
            if (correctAnswerCount != answerCount) {
                roundCount++
                currentFlashcardList = currentFlashcardList.filter { it.correctAnswer == false }.toMutableList()
                answerCount = 0
                wrongAnswerCount = 0
                currentFlashcard = currentFlashcardList.first()
                currentFlashcardIndex = 0
                flashcardListSize = currentFlashcardList.size
                flashcardText = currentFlashcard.question
                revealed = false
                expanded = false
                openDeckEndDialog.value = false
            } else {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        },"przycisk" ,text)

        //coś tam, zerowanie itp.


    }
}*/

@Composable
fun DeckEndScreen(onButtonPressed: () -> Unit, deckEndText: String, buttonText: String,
                  summaryText: String, roundList: List<Round>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Koniec talii",
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            fontFamily = libreBaskervilleFontFamily,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        Text(text = summaryText)
        Column {
            for (round in roundList) {
                Text(text = "Runda ${round.roundNumber}: ${round.correctAnswerCount}/" +
                        "${round.roundListSize}")
            }
        }
        Text(
            text = deckEndText,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
        Button(
            onClick = { onButtonPressed() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = buttonText)
        }
    }
}

@Composable
fun RoundListItem(text: String) {
    Text(text = text)
}

@Composable
fun MultipleLinearProgressIndicator(
    modifier: Modifier = Modifier,
    primaryProgress: Float,
    secondaryProgress: Float,
    primaryColor: Color = LocalColors.current.wrongButton,
    secondaryColor: Color = LocalColors.current.correctButton,
    backgroundColor: Color = primaryColor,
    clipShape: Shape = RoundedCornerShape(16.dp)
) {
    Box(
        modifier = modifier
            .clip(clipShape)
            .background(backgroundColor)
            .height(6.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(secondaryColor)
                .fillMaxHeight()
                .fillMaxWidth(secondaryProgress)
        )
        Box(
            modifier = Modifier
                .background(primaryColor)
                .fillMaxHeight()
                .fillMaxWidth(primaryProgress)
        )
    }
}

@Composable
fun Flashcard(text: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .shadow(
                10.dp,
                RoundedCornerShape(24.dp)
            )
            .background(
                color = LocalColors.current.flashcardBackground,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)

    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontFamily = montserratFontFamily
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    //DeckEndScreen({}, "koniec talii", "przycisk")
    RoundListItem("Runda: 1 3/5")
}