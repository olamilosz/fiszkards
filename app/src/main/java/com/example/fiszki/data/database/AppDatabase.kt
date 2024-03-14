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

@Database(entities = [Deck::class, Flashcard::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        @Volatile
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

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            if (instance == null) {
                val database = synchronized(this) {
                    val database = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "app_database.db"
                    ).addCallback(DatabaseCallback(scope))
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()

                    instance = database
                }
            }

            return instance as AppDatabase
        }

        private class DatabaseCallback(val scope: CoroutineScope): RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                Log.d("AppDatabase", "DatabaseCallback CoroutineScope")
                super.onCreate(db)
            }
        }
    }

    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
}