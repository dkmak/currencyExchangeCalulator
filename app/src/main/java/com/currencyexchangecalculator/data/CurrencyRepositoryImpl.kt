package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.dto.BookDTO
import com.currencyexchangecalculator.data.dto.toCurrencyModel
import com.currencyexchangecalculator.data.dto.toDomain
import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.CurrencyRepository
import com.currencyexchangecalculator.domain.CurrencyResult
import com.currencyexchangecalculator.domain.toCurrenciesDomainError
import com.currencyexchangecalculator.domain.toCurrencyDomainError
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

class CurrencyRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CurrencyRepository {
    override fun getCurrency(code: String): Flow<CurrencyResult> = flow<CurrencyResult> {
        val response: List<BookDTO?> = apiClient.getCurrency(code)
        val bookResult = response.mapNotNull { dto ->
            dto?.toDomain()
        }.first()
        emit(CurrencyResult.CurrencySuccess(bookResult))
    }.catch { throwable ->
        if (throwable is CancellationException) {
            throw throwable
        }
        emit(throwable.toCurrencyDomainError())
    }.flowOn(ioDispatcher)

    override fun getCurrencies(): Flow<CurrenciesResult> = flow<CurrenciesResult> {
        val response  = apiClient.getCurrencies()
        val result = response.map{ currencyString ->
            currencyString.toCurrencyModel()
        }
        emit(CurrenciesResult.CurrenciesSuccess(result))
    }.catch{ throwable ->
        if (throwable is CancellationException) {
            throw throwable
        }
        emit(throwable.toCurrenciesDomainError())
    }.flowOn(ioDispatcher)
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CurrencyRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository
}
