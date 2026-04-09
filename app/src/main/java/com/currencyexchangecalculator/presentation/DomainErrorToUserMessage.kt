package com.currencyexchangecalculator.presentation

import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.CurrencyResult

fun CurrencyResult.CurrencyError.currencyErrorToUserMessage(): String{
    return when (this){
        is CurrencyResult.CurrencyError.Backend -> "We are having trouble reaching our servers right now. Please try again."
        is CurrencyResult.CurrencyError.Network -> "Please check your internet connection and try again."
        is CurrencyResult.CurrencyError.Unknown -> "An unknown error occurred."
    }
}

fun CurrenciesResult.CurrenciesError.currenciesErrorToUserMessage(): String {
    return when (this){
        is CurrenciesResult.CurrenciesError.Backend -> "We are having trouble reaching our servers right now. Please try again."
        is CurrenciesResult.CurrenciesError.Network -> "Please check your internet connection and try again."
        is CurrenciesResult.CurrenciesError.Unknown -> "An unknown error occurred."
    }
}