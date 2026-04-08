package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.dto.BookDTO
import com.currencyexchangecalculator.data.dto.toDomain
import com.currencyexchangecalculator.domain.CurrencyResult
import com.currencyexchangecalculator.domain.toCurrencyDomainError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import kotlin.collections.map

class CurrencyRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    fun getCurrency(): Flow<CurrencyResult> = flow<CurrencyResult> {
        val response: List<BookDTO?> = apiClient.getBooks()
        val result = response.map { dto ->
            dto?.toDomain()
        }
        emit(CurrencyResult.CurrencySuccess(result))
    }.catch { throwable ->
        if (throwable is CancellationException){
            throw throwable
        }
        emit(throwable.toCurrencyDomainError())
    }
}