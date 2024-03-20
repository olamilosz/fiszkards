package com.example.fiszki.ui.flashcard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.R
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.data.database.entity.Round
import com.example.fiszki.ui.konfetti.Presets
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily
import com.example.fiszki.ui.theme.montserratFontFamily
import com.example.fiszki.ui.theme.secondaryColor
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class FlashcardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.extras?.getLong("deckId")
        val resetProgress = intent.extras?.getBoolean("resetProgress")

        setContent {
            FlashcardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        FlashcardScreen(deckId, resetProgress)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(deckId: Long?, resetProgress: Boolean?) {
    val flashcardViewModel: FlashcardViewModel =
        viewModel(factory = FlashcardViewModel.Factory(deckId, resetProgress))
    val uiState by flashcardViewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (!uiState.isDeckEnd && uiState.deck != null) {
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { (context as? Activity)?.finish() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            flashcardViewModel.openSettingsDialog()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = Color.Black
                            )
                        }
                    }
                )
            }
        ) {
            Column(
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
                        primaryProgress = uiState.currentWrongAnswerProgress,
                        secondaryProgress = uiState.currentAnswerProgress,
                        backgroundColor = LocalColors.current.grey,
                        modifier = Modifier
                            .weight(1f)
                    )

                    Text(
                        text = "${uiState.currentAnswerCount}/${uiState.currentFlashcardListSize}",
                        textAlign = TextAlign.Right,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)

                ) {
                    Flashcard(
                        uiState.currentFrontText,
                        uiState.currentBackText,
                        uiState.isCurrentFlashcardFlipped
                    )
                }
                Column(
                    modifier = Modifier.height(48.dp)
                ) {
                    AnimatedVisibility(
                        visible = uiState.isCurrentAnswerRevealed,
                        enter = slideInVertically (
                            initialOffsetY = {
                                it / 2
                            }

                        ) + fadeIn(
                            // Fade in with the initial alpha of 0.3f.
                            initialAlpha = 0.3f
                        ),
                        exit = slideOutVertically (
                            targetOffsetY = {
                                it
                            }
                        ) + fadeOut(targetAlpha = 0f)

                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            //if (uiState.isCurrentAnswerRevealed) {
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
        }

    } else {
        if (uiState.deck != null) {
            if (uiState.isDeckCompleted) {
                flashcardViewModel.explode()
            }

            DeckEndScreen(
                {
                    if (uiState.isDeckCompleted) {
                        (context as FlashcardActivity).finish()
                    } else {
                        flashcardViewModel.onDeckEndButtonClicked()
                    }
                },
                uiState.summaryText,
                uiState.deckEndButtonText,
                flashcardViewModel.getSummaryResultText(),
                uiState.roundList,
                uiState.isDeckCompleted,
                uiState.deckEndTitle,
                flashcardViewModel.getFlashcardsLeftToCompleteText(),
                uiState.totalCorrectAnswerCount,
                uiState.initialFlashcardListSize
            )
        } else {
            Text(text = "Błąd")
        }
    }

    when {
        uiState.showChooseModeDialog -> {
            AlertDialog(
                onDismissRequest = { flashcardViewModel.closeSettingsDialog() }
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = LocalColors.current.flashcardBackground,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp, 30.dp, 20.dp, 20.dp)

                ) {
                    val currentSelectedOption = remember { mutableStateOf(uiState.questionFirstMode) }

                    Text(
                        text = "Tryb nauki",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(10.dp, 10.dp, 10.dp, 16.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                currentSelectedOption.value = true
                            }
                    ) {
                        RadioButton(
                            selected = currentSelectedOption.value,
                            onClick = null,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "Pytanie ➞ Odpowiedź")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 10.dp, 10.dp, 20.dp)
                            .clickable {
                                currentSelectedOption.value = false
                            }
                    ) {
                        RadioButton(
                            selected = !currentSelectedOption.value,
                            onClick = null,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(text = "Odpowiedź ➞ Pytanie")
                    }

                    Button(
                        onClick = {
                            flashcardViewModel.closeSettingsDialogAndUpdate(
                                currentSelectedOption.value
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text(text = "Potwierdź")
                    }
                }
            }
        }
    }
}

@Composable
fun DeckEndScreen(onButtonPressed: () -> Unit, deckEndText: String, buttonText: String,
                  summaryText: String, roundList: List<Round>, isDeckCompleted: Boolean,
                  deckEndTitle: String, flashcardsLeftToCompleteText: String,
                  correctAnswerCount: Int, flashcardListSize: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (isDeckCompleted) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = Presets.explode(),
                updateListener = null
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize()
        ) {
            if (isDeckCompleted) {
                Image(
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.win),
                    contentDescription = "Win"
                )

            } else {
                Image(
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    painter = painterResource(id = R.drawable.keep_going),
                    contentDescription = "Keep going"
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .weight(1f)
            ) {
                Text(
                    text = deckEndTitle,
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontFamily = libreBaskervilleFontFamily,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = "Wynik:",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                DeckEndScreenProgress(correctAnswerCount, flashcardListSize)
            }
            if (!isDeckCompleted) {
                Text(
                    text = flashcardsLeftToCompleteText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
            }
            Button(
                onClick = { onButtonPressed() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun Flashcard(textFront: String, textBack: String, isFlipped: Boolean) {
    val rotation = animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        )
    )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            }
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
        if (rotation.value <= 90f) {
            Text(
                text = textFront,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontFamily = montserratFontFamily,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            )
        } else {
            Text(
                text = textBack,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontFamily = montserratFontFamily,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .graphicsLayer {
                        rotationY = 180f
                    }
            )
        }
    }
}
@Composable
fun DeckEndScreenProgress(correctAnswerCount: Int, flashcardListSize: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        MultipleLinearProgressIndicator(
            primaryProgress = correctAnswerCount / flashcardListSize.toFloat(),
            secondaryProgress = 1f,
            primaryColor = LocalColors.current.correctButton,
            secondaryColor = LocalColors.current.wrongButton,
            backgroundColor = LocalColors.current.grey,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$correctAnswerCount / $flashcardListSize",
            textAlign = TextAlign.Right,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
        )
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

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun Flashcard(flashcard: Flashcard, flipped: Boolean, questionFirstMode: Boolean,
              revealed: Boolean) {
    val question = if (questionFirstMode) flashcard.question else flashcard.answer
    val answer = if (questionFirstMode) flashcard.answer else flashcard.question
    Log.d(TAG, "q: ${flashcard.question} a: ${flashcard.answer}")
    Log.d(TAG, "question first: $questionFirstMode")
    Log.d(TAG, "flipped: $flipped")
    Log.d(TAG, "revealed: $revealed")

    val rotation = animateFloatAsState(
        targetValue = if (flipped) 0f else 180f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        ),
        label = "Flip"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            rotationY = rotation.value
            cameraDistance = 12f * density
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(5.dp)
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
            if (rotation.value <= 90f) {
                Text(
                    text = answer,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = montserratFontFamily
                )
            } else {
                Text(
                    text = question,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = montserratFontFamily,
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f
                    }
                )
            }
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun Flashcard2(question: String, answer: String, flipped: Boolean, revealed: Boolean,
               switchCurrentFlashcardFrontFaced: () -> Unit) {
    if (revealed) switchCurrentFlashcardFrontFaced()

    val rotation = animateFloatAsState(
        targetValue = if (flipped) 0f else 180f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing,
        ),
        label = "Flip"
    )

    Box(
        modifier = Modifier.graphicsLayer {
            rotationY = rotation.value
            cameraDistance = 12f * density
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(5.dp)
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
            if (rotation.value <= 90f) {
                Text(
                    text = answer,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = montserratFontFamily
                )
            } else {
                Text(
                    text = question,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontFamily = montserratFontFamily,
                    modifier = Modifier.graphicsLayer {
                        rotationY = 180f
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    val roundList = mutableListOf<Round>()
    roundList.add(Round(1, 1, 5))
    roundList.add(Round(2, 2, 4))
    roundList.add(Round(3, 2, 2))

    DeckEndScreen(
        {},
        "Deck End Texk",
        "Rozpocznij koleją rundę",
        "Summary Text",
        roundList,
        false,
        "Tytuł",
        "Zostało Ci 5 fiszek do ukończenia talii!",
        5,
        7
    )
    /*val flashcard = Flashcard(0L, 0L, "question", "answer", null)
    val deck = Deck(0L, "Nazwa")
    FlashcardScreen(deckId = 0L, resetProgress = false)*/
}