package com.currencyexchangecalculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface BookDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Query("SELECT * FROM BookEntity WHERE exchangeCurrency = :currency")
    suspend fun getBook(currency: String): BookEntity
}