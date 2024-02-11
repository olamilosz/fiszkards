package com.example.fiszki.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fiszki.data.database.dao.DeckDao
import com.example.fiszki.data.database.dao.DeckFlashcardDao
import com.example.fiszki.data.database.dao.FlashcardDao
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.DeckFlashcard
import com.example.fiszki.data.database.entity.Flashcard

@Database(entities = [Deck::class, Flashcard::class, DeckFlashcard::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java,"app_database.db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }

            return instance as AppDatabase
        }
    }

    abstract fun deckDao(): DeckDao
    abstract fun deckFlashcardDao(): DeckFlashcardDao
    abstract fun flashcardDao(): FlashcardDao
}
