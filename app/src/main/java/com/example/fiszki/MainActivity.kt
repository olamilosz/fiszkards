package com.example.fiszki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.DeckFlashcard
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckList = mutableListOf<Deck>()

        val flashcardList = mutableListOf<Flashcard>()
        flashcardList.add(Flashcard(0,  "kot", "cat", null))
        flashcardList.add(Flashcard(0, "pies", "dog", null))
        flashcardList.add(Flashcard(0, "dom", "house", null))
        flashcardList.add(Flashcard(0, "okno", "window", null))
        flashcardList.add(Flashcard(0, "stół", "table", null))
        flashcardList.add(Flashcard(0, "noc", "night", null))

        //deckList.add(firstDeck)
        //deckList.add(secondDeck)
        val firstDeck = Deck(0, "Angielski")
        val secondDeck = Deck(0, "Angielski 2")

        val database = AppDatabase.getInstance(this)

        /*//tworzymy zestaw fiszek
        val firstDeckId = database.deckDao().insertWithId(firstDeck)
        val secondDeckId = database.deckDao().insertWithId(secondDeck)

        for (flashcard in flashcardList) {
            //dodajemy fiszkę do bazy
            val flashcardId = database.flashcardDao().insertWithId(flashcard)

            //dodajemy tą fiszkę do 1 i 2 zestawu
            database.deckFlashcardDao().insert(DeckFlashcard(firstDeckId, flashcardId))
            database.deckFlashcardDao().insert(DeckFlashcard(secondDeckId, flashcardId))
        }*/

        val decks = database.deckDao().getAll()
        Log.d("decks", decks.toString())

        val flashcards = database.flashcardDao().getAll()
        Log.d("flashcards", flashcards.toString())

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    FlashcardApp(decks)
                }
            }
        }
    }
}

@Composable
fun FlashcardApp(deckList: List<Deck>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Zestawy fiszek",
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontFamily = libreBaskervilleFontFamily,
                fontWeight = FontWeight.Bold
            )
            deckList.forEach {
                FlashcardDeckListItem(deck = it)
            }
        }
    }
}

@Composable
fun FlashcardDeckListItem(deck: Deck) {
    val localContext = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = deck.deckName,
            modifier = Modifier
                .weight(1f)
        )
        Button(onClick = {
            val intent = Intent(localContext, FlashcardActivity()::class.java)
            intent.putExtra("deckId", deck.id)
            localContext.startActivity(intent)

        }) {
            Text(text = "Otwórz")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val deck = Deck(1, "Angielski")
    FlashcardDeckListItem(deck = deck)
}