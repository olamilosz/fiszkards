package com.example.fiszki

import android.app.Application
import androidx.room.Room
import com.example.fiszki.data.database.AppDatabase

class FlashcardApp : Application() {
    val appDatabase: AppDatabase by lazy { AppDatabase.getInstance(this) }
}