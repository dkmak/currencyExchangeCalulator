package com.currencyexchangecalculator.data.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application
    ): AppDatabase {
        return Room
            .databaseBuilder(application, AppDatabase::class.java, "Database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideBookDao(appDatabase: AppDatabase): BookDAO = appDatabase.bookDAO()

}