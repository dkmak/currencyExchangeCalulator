package com.currencyexchangecalculator.data.dto

import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrencyModel
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

private fun parseBookForCurrency (book: String): Pair<CurrencyModel, CurrencyModel> {
    val bookParts = book.split("_")

    if (bookParts.size != 2) { // throw an error instead?
        return (CurrencyModel.Unknown(label = "Unknown Code", code = book) to
                CurrencyModel.Unknown(label ="Unknown Code",code = book))
    }

    val baseCurrency = bookParts[0].toCurrencyModel()
    val exchangeCurrency = bookParts[0].toCurrencyModel()
    return (baseCurrency to exchangeCurrency)
}

private fun String.toCurrencyModel(): CurrencyModel {
    return when (this){
        "usdc" -> CurrencyModel.USDC
        "mxn" -> CurrencyModel.MXN
        else -> CurrencyModel.Unknown(label ="Unknown Code", code = this)
    }
}
