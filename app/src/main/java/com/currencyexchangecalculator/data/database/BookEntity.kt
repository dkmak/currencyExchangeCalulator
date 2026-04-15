package com.currencyexchangecalculator.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.currencyexchangecalculator.data.dto.toCurrencyModel
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.Currency

@Entity
data class BookEntity (
    val ask: String,
    val bid: String,
    val baseCurrency: String,
    @PrimaryKey val exchangeCurrency: String,
    val date: String
)

fun BookEntity.toBook(): Book{
    return Book(
        ask = this.ask,
        bid = this.bid,
        baseCurrency = this.baseCurrency.toCurrencyModel(),
        exchangeCurrency = this.exchangeCurrency.toCurrencyModel(),
        date = this.date
    )
}