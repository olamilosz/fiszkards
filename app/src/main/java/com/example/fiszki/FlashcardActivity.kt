package com.example.fiszki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.ui.theme.montserratFontFamily

class FlashcardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*flashcardList.add(Flashcard("kot", "cat"))
        flashcardList.add(Flashcard("pies", "dog"))
        flashcardList.add(Flashcard("dom", "house"))
        flashcardList.add(Flashcard("okno", "window"))
        flashcardList.add(Flashcard("stół", "table"))
        flashcardList.add(Flashcard("noc", "night"))
        val flashcardDeck = FlashcardDeckModel("Angielski", flashcardList)*/

        val deckId = intent.extras?.getLong("deckId")
        val database = AppDatabase.getInstance(this)
        val flashcardList = mutableListOf<Flashcard>()
        var deckName = ""

        if (deckId != null) {
            val deck = AppDatabase.getInstance(this).deckDao().getDeckById(deckId)

            if (deck != null) {
                deckName = deck.deckName
                val flashcardDeckList = database.deckFlashcardDao().getFlashcardsByDeckId(deckId)

                if (flashcardDeckList.isNotEmpty()) {
                    for (flashcardDeck in flashcardDeckList) {
                        val flashcard = database.flashcardDao().getFlashcardById(flashcardDeck.flashcardId)
                        flashcardList.add(flashcard)
                    }
                }
            }
        }

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
    var currentProgress by remember { mutableStateOf(0f) }
    var expanded by remember { mutableStateOf(false) }
    var currentFlashcard by remember { mutableStateOf(flashcardList.first()) }
    var currentFlashcardIndex by remember { mutableStateOf(0) }
    val flashcardListSize = flashcardList.size
    var flashcardText by remember { mutableStateOf(currentFlashcard.question) }
    var revealed by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

        LinearProgressIndicator(
            (currentFlashcardIndex + 1) / flashcardListSize.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)

        ) {
            Flashcard(text = flashcardText, turned = !expanded)
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (currentFlashcardIndex < flashcardListSize - 1) {
                        currentFlashcardIndex++
                        currentFlashcard = flashcardList[currentFlashcardIndex]
                        flashcardText = currentFlashcard.question
                        expanded = false
                        Log.d("size", "current $currentFlashcardIndex size $flashcardListSize ")
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
                    if (currentFlashcardIndex < flashcardListSize - 1) {
                        currentFlashcardIndex++
                        currentFlashcard = flashcardList[currentFlashcardIndex]
                        flashcardText = currentFlashcard.question
                        expanded = false
                        Log.d("size", "current $currentFlashcardIndex size $flashcardListSize ")
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
}

@Composable
fun Flashcard(text: String, turned: Boolean) {
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

    /*flashcardList.add(Flashcard("kot", "cat"))
    flashcardList.add(Flashcard("pies", "dog"))
    flashcardList.add(Flashcard("dom", "house"))
    flashcardList.add(Flashcard("okno", "window"))
    flashcardList.add(Flashcard("stół", "table"))
    flashcardList.add(Flashcard("noc", "night"))

    FlashcardScreen(
        FlashcardDeckModel("fewfewfawe", flashcardList),
        "Lorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit ametLorem ipsum dolor sit amet22222",
        "cos tam cos tam...."
    )*/

}