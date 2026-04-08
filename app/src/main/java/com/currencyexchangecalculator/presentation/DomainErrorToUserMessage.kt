package com.currencyexchangecalculator.presentation

import com.currencyexchangecalculator.domain.CurrencyResult

fun CurrencyResult.CurrencyError.toUserMessage(): String{
    return when (this){
        is CurrencyResult.CurrencyError.Backend -> "We are having trouble reaching our servers right now. Please try again"
        is CurrencyResult.CurrencyError.Network -> "Please check your internet connection and try again"
        is CurrencyResult.CurrencyError.Unknown -> "An unknown error occurred"
    }
}