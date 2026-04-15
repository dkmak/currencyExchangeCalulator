package com.currencyexchangecalculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.currencyexchangecalculator.domain.Book
import java.nio.charset.CodingErrorAction.REPLACE

@Dao
interface BookDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT * FROM BookEntity WHERE exchangeCurrency = :code")
    suspend fun getBookOrNull(code: String): BookEntity?

    @Query("SELECT * FROM BookEntity")
    suspend fun getAllBooks(): List<BookEntity>
}