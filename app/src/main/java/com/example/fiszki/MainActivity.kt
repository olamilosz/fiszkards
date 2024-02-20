package com.example.fiszki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.ui.home.HomeScreen
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.viewmodel.DeckViewModel

class MainActivity : ComponentActivity() {
    private val deckViewModel: DeckViewModel by viewModels { DeckViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var deckList = mutableListOf<Deck>()

        deckViewModel.allDecksLiveData.observe(this) { items ->
            items?.let {
                deckList = items
            }
        }

        val database = AppDatabase.getInstance(this)
        val decks = database.deckDao().getAll()
        val flashcards = database.flashcardDao().getAll()
        Log.d("decks", decks.toString())
        Log.d("flashcards", flashcards.toString())

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    //FlashcardApp(decks)
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(deckViewModel: DeckViewModel = viewModel()) {
    val allDecks by deckViewModel.allDecksLiveData.observeAsState()
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