package com.currencyexchangecalculator.domain

sealed interface CurrencyResult {
    data class CurrencySuccess(val books: List<Book>): CurrencyResult

    sealed interface CurrencyError: CurrencyResult {
        data object Network : CurrencyError
        data object Backend : CurrencyError
        data object Unknown : CurrencyError
    }
}
