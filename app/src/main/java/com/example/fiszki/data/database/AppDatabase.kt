package com.example.fiszki.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fiszki.data.database.dao.DeckDao
import com.example.fiszki.data.database.dao.FlashcardDao
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Deck::class, Flashcard::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): AppDatabase {
            synchronized(this) {
                var databaseInstance = instance
                if (databaseInstance == null) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "app_database.db"
                    )
                    .addCallback(PrepopulateRoomCallback(context))
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    databaseInstance = instance
                }
                return databaseInstance as AppDatabase
            }
        }
    }

    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
}