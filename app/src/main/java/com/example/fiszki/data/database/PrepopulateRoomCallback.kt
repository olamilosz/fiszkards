package com.example.fiszki.data.database

import android.content.Context
import android.util.Log
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fiszki.R
import com.example.fiszki.data.database.entity.Deck
import com.example.fiszki.data.database.entity.Flashcard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class PrepopulateRoomCallback(private val context: Context) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        CoroutineScope(Dispatchers.IO).launch {
            prepopulateData(context)
        }
    }

    private suspend fun prepopulateData(context: Context) {
        try {
            val deckDao = AppDatabase.getDatabase(context).deckDao()
            val flashcardDao = AppDatabase.getDatabase(context).flashcardDao()

            val deckList: JSONArray =
                context.resources.openRawResource(R.raw.decklist).bufferedReader().use {
                    JSONArray(it.readText())
                }

            deckList.takeIf { it.length() > 0 }?.let { list ->
                for (i in 0 until list.length()) {
                    val deckObject = list.getJSONObject(i)

                    val deckId = deckDao.insertWithId(
                        Deck(
                            0,
                            deckObject.getString("deck_name"),
                            false
                        )
                    )

                    val flashcardList = deckObject.getJSONArray("flashcard_list")

                    flashcardList.takeIf { it.length() > 0 }?.let { flashcards ->
                        for (j in 0 until flashcards.length()) {
                            val flashcardObject = flashcards.getJSONObject(j)

                            flashcardDao.insert(
                                Flashcard(
                                    0,
                                    deckId,
                                    flashcardObject.getString("question"),
                                    flashcardObject.getString("answer"),
                                    null
                                )
                            )
                        }
                        Log.e("User App", "FLASHCARDS successfully pre-populated users into database")
                    }

                }
                Log.e("User App", "DECKS successfully pre-populated users into database")
            }


        } catch (exception: Exception) {
            Log.e(
                "User App",
                exception.localizedMessage ?: "DECKS failed to pre-populate users into database"
            )
        }
    }
}