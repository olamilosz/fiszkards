package com.example.fiszki

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.ui.deck.DeckActivity
import com.example.fiszki.ui.flashcard.FlashcardActivity
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.ui.deck.DeckViewModel
import com.example.fiszki.ui.home.HomeViewModel
import com.example.fiszki.ui.theme.LocalColors

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val homeViewModel: HomeViewModel =
        viewModel(factory = HomeViewModel.Factory())
    val deckList = homeViewModel.allDecksLiveData.observeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 36.dp, 24.dp, 24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Zestawy fiszek",
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontFamily = libreBaskervilleFontFamily,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            deckList.value?.let { deck ->
                deck.forEach {
                    FlashcardDeckListItem2(deck = it)
                }
            }
        }
    }
}

@Composable
fun FlashcardDeckListItem2(deck: Deck) {
    val localContext = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                2.dp,
                RoundedCornerShape(10.dp)
            )
            .background(
                color = LocalColors.current.flashcardBackground,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {
                val intent = Intent(localContext, DeckActivity::class.java)
                intent.putExtra("deckId", deck.id)
                localContext.startActivity(intent)
            }
            .padding(16.dp)
    ) {
        Text(
            text = deck.deckName,
            modifier = Modifier
                .weight(1f)
        )

        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Otwórz talię")
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
            val intent = Intent(localContext, DeckActivity::class.java)
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
    FlashcardDeckListItem2(deck = deck)
}