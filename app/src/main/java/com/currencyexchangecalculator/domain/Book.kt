package com.currencyexchangecalculator.domain

data class Book (
    val ask: String,
    val bid: String,
    val baseCurrency: CurrencyModel,
    val exchangeCurrency: CurrencyModel,
    val date: String
)