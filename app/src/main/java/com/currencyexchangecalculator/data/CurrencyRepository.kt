package com.currencyexchangecalculator.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    fun getCurrency(): Flow<List<CurrencyDTO?>?> = flow{
        val response = apiClient.getCurrencies()
        emit(response)
    }.catch { throwable ->
        throw throwable
    }
}