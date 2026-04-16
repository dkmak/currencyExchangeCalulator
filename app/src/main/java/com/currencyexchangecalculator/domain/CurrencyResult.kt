package com.currencyexchangecalculator.domain

sealed interface CurrencyResult {
    data class CurrencySuccess(
        val book: Book,
        val warning: CurrencyError? = null,
        val isFromCache: Boolean = false
    ): CurrencyResult

    sealed interface CurrencyError: CurrencyResult {
        data object Network : CurrencyError
        data object Backend : CurrencyError
        data object Unknown : CurrencyError
    }
}

sealed interface CurrenciesResult {
    data class CurrenciesSuccess(val currencies: List<Currency>): CurrenciesResult

    sealed interface CurrenciesError: CurrenciesResult {
        data object Network : CurrenciesError
        data object Backend : CurrenciesError
        data object Unknown : CurrenciesError
    }
}
