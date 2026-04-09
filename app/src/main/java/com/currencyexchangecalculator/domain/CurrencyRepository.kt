package com.currencyexchangecalculator.domain

import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getCurrency(code: String): Flow<CurrencyResult>
    fun getCurrencies(): Flow<CurrenciesResult>
}