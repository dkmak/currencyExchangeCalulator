package com.currencyexchangecalculator.data.dto

import com.currencyexchangecalculator.data.database.BookEntity
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.Currency
import kotlinx.serialization.Serializable

@Serializable
data class BookDTO(
    val ask: String,
    val bid: String,
    val book: String,
    val date: String
)

fun BookDTO.toDomain(): Book {
    val (baseCurrency, exchangeCurrency) = parseBookForCurrency(this.book)

    return Book(
        ask = this.ask,
        bid = this.bid,
        baseCurrency = baseCurrency,
        exchangeCurrency = exchangeCurrency,
        date = this.date
    )
}

fun BookDTO.toEntity(): BookEntity {
    val bookParts = book.split("_")

    return BookEntity(
        ask = this.ask,
        bid = this.bid,
        baseCurrency = bookParts[0].uppercase(),
        exchangeCurrency = bookParts[1].uppercase(),
        date = this.date
    )
}

private fun parseBookForCurrency (book: String): Pair<Currency, Currency> {
    val bookParts = book.split("_")

    if (bookParts.size != 2) {
        return (Currency.Unknown(label = "Unknown Currency", code = book) to
                Currency.Unknown(label ="Unknown Currency",code = book))
    }

    val baseCurrency = bookParts[0].toCurrencyModel()
    val exchangeCurrency = bookParts[1].toCurrencyModel()
    return (baseCurrency to exchangeCurrency)
}

fun String.toCurrencyModel(): Currency {
    return when (uppercase()){
        "USDC" -> Currency.USDC
        "MXN" -> Currency.MXN
        "ARS" -> Currency.ARS
        "EURC" -> Currency.EURC
        "BRL" -> Currency.BRL
        "COP" -> Currency.COP
        else -> Currency.Unknown(label ="Unknown Currency", code = this)
    }
}
