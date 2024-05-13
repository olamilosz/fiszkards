package com.example.fiszki

import android.app.Application
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.Repository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FlashcardApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: Repository by lazy { Repository(database.deckDao(), database.flashcardDao()) }
}