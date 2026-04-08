package com.currencyexchangecalculator.data.dto

import com.currencyexchangecalculator.domain.Book
import kotlinx.serialization.Serializable

@Serializable
data class BookDTO(
    val ask: String,
    val bid: String,
    val book: String,
    val date: String
)

fun BookDTO.toDomain(): Book {
    return Book(
        ask = this.ask,
        bid = this.bid,
        book = this.book,
        date = this.date
    )
}
