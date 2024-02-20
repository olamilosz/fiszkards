package com.example.fiszki.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.fiszki.FlashcardActivity
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.viewmodel.DeckViewModel

@Composable
fun HomeScreen(deckViewModel: DeckViewModel = viewModel()) {
    val deckList = deckViewModel.allDecksLiveData.observeAsState()

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
            deckList.value?.let { deck ->
                deck.forEach {
                    FlashcardDeckListItem(deck = it)
                }
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
fun Preview() {
    val deck = Deck(1, "Angielski")
    FlashcardDeckListItem(deck = deck)
}