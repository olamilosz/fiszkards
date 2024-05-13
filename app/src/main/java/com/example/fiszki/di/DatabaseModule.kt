package com.example.fiszki.di

import android.content.Context
import androidx.room.Room
import com.example.fiszki.data.database.AppDatabase
import com.example.fiszki.data.database.PrepopulateRoomCallback
import com.example.fiszki.data.database.dao.DeckDao
import com.example.fiszki.data.database.dao.FlashcardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideFlashcardDao(database: AppDatabase): FlashcardDao {
        return database.flashcardDao()
    }

    @Provides
    fun provideDeckDao(database: AppDatabase): DeckDao {
        return database.deckDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database.db"
        )
            .addCallback(PrepopulateRoomCallback(context))
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}

