package com.example.fiszki.di

import com.example.fiszki.data.database.Repository
import com.example.fiszki.data.database.RepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(repository: Repository): RepositoryInterface
}