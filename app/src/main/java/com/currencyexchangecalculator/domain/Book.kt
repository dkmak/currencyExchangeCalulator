package com.currencyexchangecalculator.domain

data class Book (
    val ask: String,
    val bid: String,
    val baseCurrency: Currency,
    val exchangeCurrency: Currency,
    val date: String
)