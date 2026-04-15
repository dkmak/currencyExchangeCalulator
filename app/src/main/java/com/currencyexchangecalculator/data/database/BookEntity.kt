package com.currencyexchangecalculator.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.currencyexchangecalculator.domain.Currency

@Entity
data class BookEntity(
    val ask: String,
    val bid: String,
    val baseCurrency: String,
    @PrimaryKey val exchangeCurrency: String,
    val date: String
)