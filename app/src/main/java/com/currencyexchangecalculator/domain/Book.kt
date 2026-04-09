package com.currencyexchangecalculator.domain

data class Book (
    val ask: String,
    val bid: String,
    val baseCurrency: CurrencyModel,
    val exchangeCurrency: CurrencyModel,
    val book: String,
    val date: String
)