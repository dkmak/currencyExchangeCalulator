package com.currencyexchangecalculator

import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.CurrencyRepository
import com.currencyexchangecalculator.domain.CurrencyResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeCurrencyRepository: CurrencyRepository {
    var currencyResult: CurrencyResult = CurrencyResult.CurrencyError.Unknown
    var currenciesResult: CurrenciesResult = CurrenciesResult.CurrenciesError.Unknown

    var lastCode: String = ""
        private set

    override fun getCurrency(code: String): Flow<CurrencyResult> {
        lastCode = code
        return flowOf(currencyResult)
    }

    override fun getCurrencies(): Flow<CurrenciesResult> {
        return flowOf(currenciesResult)
    }
}