package com.example.fiszki.ui.deck

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fiszki.data.database.entity.Flashcard
import com.example.fiszki.ui.flashcard.FlashcardActivity
import com.example.fiszki.ui.theme.FlashcardTheme
import com.example.fiszki.ui.theme.LocalColors
import com.example.fiszki.ui.theme.libreBaskervilleFontFamily

class DeckActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deckId = intent.extras?.getLong("deckId")
        Log.d(TAG, "deckId: $deckId")

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

    deckViewModel.flashcardListLiveData.observe(context as DeckActivity) {
        deckViewModel.updateFlashcardList(it)
    }

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
                                text = uiState.deckName,
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },
                        actions = {
                            if (uiState.deck!!.userCreated) {
                                IconButton(
                                    onClick = { deckViewModel.showDropdownMenu() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "More",
                                        tint = Color.Black
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = uiState.isMenuExpanded,
                                onDismissRequest = { deckViewModel.hideDropdownMenu() },
                                modifier = Modifier
                                    .background(Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text(text = "Edytuj nazwę ") },
                                    onClick = { deckViewModel.showEditDeckNameDialog() },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Filled.Edit,
                                            contentDescription = "Edytuj nazwę",
                                            tint = Color.Black
                                        )
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(text = "Usuń zestaw ") },
                                    onClick = { deckViewModel.showDeleteDeckDialog() },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = "Edytuj nazwę",
                                            tint = Color.Black
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (uiState.flashcardListSize != 0) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            modifier = Modifier
                                .padding(20.dp, 80.dp, 20.dp, 20.dp)
                                .fillMaxWidth(),
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
                                    text = "${uiState.correctAnswerCount}/${uiState.flashcardListSize}",
                                    textAlign = TextAlign.Right,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 1.dp)
                                )
                            }

                            ActionButton(
                                text = uiState.goToFlashcardScreenButtonText,
                                onClick = {
                                    val intent = Intent(context, FlashcardActivity::class.java)
                                    intent.putExtra("deckId", deckId)
                                    intent.putExtra(
                                        "resetProgress",
                                        deckViewModel.getResetProgressValue()
                                    )
                                    context.startActivity(intent)
                                }
                            )

                            ActionButton(
                                text = "Wyświetl listę fiszek",
                                onClick = { deckViewModel.showFlashcardListScreen() }
                            )
                        }
                    } else {
                        Text(
                            text = "Ten zestaw jest pusty.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    }
                    if (uiState.deck!!.userCreated) {
                        FloatingActionButton(
                            onClick = { deckViewModel.showAddFlashcardScreen(false) },
                            containerColor = LocalColors.current.fabButton,
                            contentColor = Color.Black,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 24.dp, bottom = 24.dp)
                        ) {
                            Icon(Icons.Filled.Add, "Floating action button.")
                        }
                    }
                }
            }
        }

        if (uiState.showFlashcardList) {
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.flashcardList.isNotEmpty()) {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                top = 20.dp,
                                bottom = 80.dp
                            ),
                            modifier = Modifier
                                .padding(20.dp, 60.dp, 20.dp, 0.dp)
                                .fillMaxWidth()
                        ) {
                            items(
                                items = uiState.flashcardList
                            ) { flashcard ->
                                FlashcardListItem(
                                    flashcard = flashcard,
                                    onEditIconClick = { deckViewModel.showEditFlashcardDialog(flashcard) },
                                    onDeleteIconClick = { deckViewModel.showDeleteFlashcardDialog(flashcard) },
                                    uiState.deck!!.userCreated
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Lista fiszek jest pusta.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentHeight(align = Alignment.CenterVertically)
                        )
                    }

                    FloatingActionButton(
                        onClick = { deckViewModel.showAddFlashcardScreen(true) },
                        containerColor = LocalColors.current.fabButton,
                        contentColor = Color.Black,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 24.dp, bottom = 24.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Add flashcard")
                    }
                    when {
                        uiState.isEditFlashcardDialogVisible -> {
                            val currentlyEditedFlashcard = uiState.currentlyEditedFlashcard
                            var question by remember { mutableStateOf(currentlyEditedFlashcard.question) }
                            var answer by remember { mutableStateOf(currentlyEditedFlashcard.answer) }
                            val focusRequester = FocusRequester()

                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }

                            AlertDialog(
                                onDismissRequest = { deckViewModel.hideEditFlashcardDialog() }
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
                                    Text(
                                        text = "Edytuj fiszkę",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )

                                    OutlinedTextField(
                                        value = question,
                                        onValueChange = {
                                            question = it
                                        },
                                        maxLines = 1,
                                        label = { Text("Pytanie") },
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    )
                                    OutlinedTextField(
                                        value = answer,
                                        onValueChange = {
                                            answer = it
                                        },
                                        maxLines = 1,
                                        label = { Text("Odpowiedź") },
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                deckViewModel.hideEditFlashcardDialog()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent,
                                                contentColor = Color.Black
                                            )
                                        ) {
                                            Text(text = "Anuluj")
                                        }
                                        Button(
                                            onClick = {
                                                if (question.isNotEmpty() && answer.isNotEmpty()) {
                                                    deckViewModel.hideEditFlashcardDialogAndUpdate(
                                                        currentlyEditedFlashcard,
                                                        question,
                                                        answer
                                                    )
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Black
                                            ),
                                            enabled = (question.isNotEmpty() && answer.isNotEmpty()
                                                    && (question != currentlyEditedFlashcard.question
                                                    || answer != currentlyEditedFlashcard.answer))
                                        ) {
                                            Text(text = "Potwierdź")
                                        }
                                    }
                                }
                            }
                        }
                        uiState.isDeleteFlashcardDialogVisible -> {
                            val currentlyDeletedFlashcard = uiState.currentlyDeletedFlashcard

                            AlertDialog(
                                onDismissRequest = { deckViewModel.hideDeleteFlashcardDialog() }
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
                                    Text(
                                        text = "Czy na pewno chcesz usunąć tę fiszkę?",
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 20.dp)
                                    )

                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            onClick = {
                                                deckViewModel.hideDeleteFlashcardDialog()
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Transparent,
                                                contentColor = Color.Black
                                            )
                                        ) {
                                            Text(text = "Anuluj")
                                        }
                                        Button(
                                            onClick = {
                                                deckViewModel.hideDeleteFlashcardDialogAndUpdate(
                                                    currentlyDeletedFlashcard
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = LocalColors.current.wrongButton
                                            )
                                        ) {
                                            Text(text = "Potwierdź")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (uiState.showAddFlashcardScreen) {
            var question by remember { mutableStateOf("") }
            var answer by remember { mutableStateOf("") }
            val focusRequester = FocusRequester()

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = Color.Black
                        ),
                        title = {
                            Text(
                                text = "Dodaj nową fiszkę",
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
                            IconButton(onClick = { deckViewModel.hideAddFlashcardScreen() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .padding(20.dp, 80.dp, 20.dp, 20.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = question,
                            onValueChange = {
                                question = it
                            },
                            maxLines = 1,
                            label = { Text("Pytanie") },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                        )
                        OutlinedTextField(
                            value = answer,
                            onValueChange = {
                                answer = it
                            },
                            maxLines = 1,
                            label = { Text("Odpowiedź") },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                deckViewModel.addFlashcard(question, answer)
                                question = ""
                                answer = ""
                                focusRequester.requestFocus()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            enabled = (question.isNotEmpty() && answer.isNotEmpty()),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Dodaj kolejną fiszkę")
                        }
                    }
                }
            }
        }
    } else {
        Text(
            text = "Ten zestaw jest pusty.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }

    when {
        uiState.isEditDeckNameDialogVisible -> {
            var text by remember { mutableStateOf(uiState.deckName) }
            var showError by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { deckViewModel.onEditDeckNameDialogDismiss() }
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
                    Text(
                        text = "Edytuj nazwę zestawu",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            if (it.isNotEmpty()) showError = false
                        },
                        maxLines = 2,
                        label = { Text("Nazwa zestawu") },
                        modifier = Modifier
                            .padding(8.dp)
                    )
                    when {
                        showError -> {
                            Text(
                                text = "Nazwa nie może być pusta.",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                deckViewModel.onEditDeckNameDialogDismiss()
                                deckViewModel.hideDropdownMenu()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(text = "Anuluj")
                        }
                        Button(
                            onClick = {
                                if (text.isEmpty()) {
                                    showError = true
                                } else {
                                    deckViewModel.onEditDeckNameDialogConfirm(text)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            ),
                            enabled = (uiState.deckName != text)
                        ) {
                            Text(text = "Potwierdź")
                        }
                    }
                }
            }
        }
        uiState.isDeleteDeckDialogVisible -> {
            AlertDialog(
                onDismissRequest = {
                    deckViewModel.hideDeleteDeckDialog()
                    deckViewModel.hideDropdownMenu()
                }
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
                    Text(
                        text = "Czy na pewno chcesz usunąć zestaw ${uiState.deckName}?",
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                deckViewModel.hideDeleteDeckDialog()
                                deckViewModel.hideDropdownMenu()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            )
                        ) {
                            Text(text = "Anuluj")
                        }
                        Button(
                            onClick = {
                                deckViewModel.hideDropdownMenu()
                                uiState.deck?.let { deckViewModel.hideDeleteDeckDialogAndUpdate(it) }
                                (context as? Activity)?.finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text(text = "Usuń")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardListItem(
    flashcard: Flashcard,
    onEditIconClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
    userCreated: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 10.dp)
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
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = flashcard.question,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = flashcard.answer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (userCreated) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edytuj",
                modifier = Modifier
                    .clickable {
                        onEditIconClick()
                    }
                    .padding(8.dp)
            )
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Usuń",
                modifier = Modifier
                    .clickable {
                        onDeleteIconClick()
                    }
                    .padding(8.dp),
                tint = LocalColors.current.wrongButton
            )
        }
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

        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Otwórz talię")
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