package com.example.fiszki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.window.Dialog
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.ui.theme.montserratFontFamily
import com.example.fiszki.viewmodel.FlashcardViewModel

class FlashcardActivity : ComponentActivity() {
    private val flashcardViewModel: FlashcardViewModel by viewModels { FlashcardViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.extras?.getLong("deckId")
        var flashcardList = mutableListOf<Flashcard>()
        var deckName = ""

        if (deckId != null) {
            val deck = AppDatabase.getInstance(this).deckDao().getDeckById(deckId)

            if (deck != null) {
                deckName = deck.deckName
                flashcardList = flashcardViewModel.getFlashcardsByDeckId(deckId)
            }
        }

        Log.d("FLASHCARDS SCREEN", "deckId $deckId name $deckName")
        Log.d("FLASHCARDS", "$flashcardList")

        //NOWE
        val flashcardUiState = flashcardViewModel.getFlashcardUiStateByDeckId(deckId)

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (flashcardList.isNotEmpty()) {
                            FlashcardScreen(deckName, flashcardList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardScreen(deckName: String, flashcardList: List<Flashcard>) {
    var wrongAnswerCount by remember { mutableIntStateOf(0) }
    var answerCount by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var currentFlashcard by remember { mutableStateOf(flashcardList.first()) }
    var currentFlashcardIndex by remember { mutableIntStateOf(0) }
    val flashcardListSize = flashcardList.size
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
                                currentFlashcardIndex++
                                currentFlashcard = flashcardList[currentFlashcardIndex]
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
                                currentFlashcardIndex++
                                currentFlashcard = flashcardList[currentFlashcardIndex]
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
        DeckEndScreen()
    }

    /*when {
        openDeckEndDialog.value -> {
            DeckEndDialog(
                onDismissRequest = {
                    Log.d("okno", "dismiss")
                                   },
                onConfirmation = {
                    Log.d("okno", "confirm")
                }
            )
        }
    }*/
}

@Composable
fun DeckEndScreen() {
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
        Text(
            text = "Ilość fiszek czy coś tam",
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )

    }
}

@Composable
fun DeckEndDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
    //painter: Painter,
    //imageDescription: String,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                /*Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(160.dp)
                )*/
                Text(
                    text = "This is a dialog with buttons and an image.",
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }

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
    val flashcardList = mutableListOf<Flashcard>()

    flashcardList.add(Flashcard(1, 0, "kot", "cat", null))
    flashcardList.add(Flashcard(1, 0, "kot2", "cat", null))
    flashcardList.add(Flashcard(1, 0,"kot3", "cat", null))
    flashcardList.add(Flashcard(1, 0,"kot4", "cat", null))

    //FlashcardScreen("fewfewfawe", flashcardList)

    DeckEndScreen()
}