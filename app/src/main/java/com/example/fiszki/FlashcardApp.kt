package com.example.fiszki

import android.app.Application
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FlashcardApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository: Repository by lazy { Repository(database.deckDao(), database.flashcardDao()) }
}