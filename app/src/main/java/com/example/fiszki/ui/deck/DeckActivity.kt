package com.example.fiszki.ui.deck

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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

@Composable
fun DeckScreen(deckId: Long?) {
    val deckViewModel: DeckViewModel =
        viewModel(factory = DeckViewModel.Factory(deckId))
    val uiState by deckViewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.deck != null) {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = {
                val intent = Intent(context, FlashcardActivity::class.java)
                intent.putExtra("deckId", deckId)
                context.startActivity(intent)
            }) {
                Text(text = "Przejdź do nauki")
            }
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
                primaryProgress = uiState.wrongAnswerProgress,
                secondaryProgress = uiState.answerProgress,
                modifier = Modifier.padding(0.dp, 10.dp),
                backgroundColor = LocalColors.current.grey
            )
            Text(text = "${uiState.answerCount} / ${uiState.flashcardListSize}")
            
        }

    } else {
        Text(text = "Błąd")
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
        DeckScreen(1L)
    }
}