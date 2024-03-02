package com.example.fiszki.ui.deck

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.MainActivity
import com.example.fiszki.R
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.ui.flashcard.FlashcardActivity
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily

class DeckActivity : ComponentActivity() {
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
                        DeckScreen(deckId)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DeckScreen(deckId: Long?) {
    val deckViewModel: DeckViewModel =
        viewModel(factory = DeckViewModel.Factory(deckId))
    val uiState by deckViewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.deck != null && deckId != null) {
        if (!uiState.showFlashcardList) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.Black
                        ),
                        title = {
                            Text(
                                text = uiState.deck!!.deckName,
                                fontSize = 24.sp,
                                color = Color.Black,
                                fontFamily = libreBaskervilleFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp),
                                maxLines = 1
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { (context as? Activity)?.finish() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier
                        .padding(20.dp, 80.dp, 20.dp, 20.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        MultipleLinearProgressIndicator(
                            primaryProgress = uiState.wrongAnswerProgress,
                            secondaryProgress = uiState.answerProgress,
                            backgroundColor = LocalColors.current.grey,
                            modifier = Modifier
                                .weight(1f)
                        )

                        Text(
                            text = "${uiState.answerCount}/${uiState.flashcardListSize}",
                            textAlign = TextAlign.Right,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
                        )
                    }

                    ActionButton(
                        text = "Przejdź do nauki",
                        onClick = {
                            val intent = Intent(context, FlashcardActivity::class.java)
                            intent.putExtra("deckId", deckId)
                            context.startActivity(intent)
                        }
                    )

                    ActionButton(
                        text = "Wyświetl listę fiszek",
                        onClick = { deckViewModel.showFlashcardListScreen() }
                    )
                }
            }
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.Black
                        ),
                        title = {
                            Text(
                                text = "Lista fiszek",
                                fontSize = 24.sp,
                                color = Color.Black,
                                fontFamily = libreBaskervilleFontFamily,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp),
                                maxLines = 1
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { deckViewModel.exitFlashcardListScreen() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            ) {
                //Lista fiszek ekran
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(
                        top = 20.dp,
                        bottom = 20.dp
                    ),
                    modifier = Modifier
                        .padding(20.dp, 60.dp, 20.dp, 0.dp)
                        .fillMaxWidth()
                ) {
                    for (flashcard in uiState.flashcardList) {
                        item {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
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
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = flashcard.question,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(text = flashcard.answer)
                            }
                        }
                    }
                }
            }
        }

    } else {
        Text(text = "Błąd")
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
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
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
        )

        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Otwórz talię")
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //DeckScreen(1L)
    }
}