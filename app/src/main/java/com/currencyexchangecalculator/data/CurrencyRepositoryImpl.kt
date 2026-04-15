package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.database.BookDAO
import com.currencyexchangecalculator.data.dto.BookDTO
import com.currencyexchangecalculator.data.dto.toCurrencyModel
import com.currencyexchangecalculator.data.dto.toDomain
import com.currencyexchangecalculator.data.dto.toEntity
import com.currencyexchangecalculator.domain.Book
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
    private val bookDAO: BookDAO,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CurrencyRepository {
/*    override fun getCurrency(code: String): Flow<CurrencyResult> = flow<CurrencyResult> {
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
    }.flowOn(ioDispatcher)*/

    override fun getCurrency(code: String): Flow<CurrencyResult> = flow<CurrencyResult> {
        try {
            val response: List<BookDTO?> = apiClient.getCurrency(code)
            // insert new cached response
            response.mapNotNull { dto ->
                dto?.let{
                    dto.toDomain()
                    bookDAO.insert(dto.toEntity())
                }
            }.first()
        } catch(throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }

            emit(CurrencyResult.CurrencySuccess(getCachedBook(code)))
            return@flow
        }

        emit(CurrencyResult.CurrencySuccess(getCachedBook(code)))
    }.flowOn(ioDispatcher)

    private suspend fun getCachedBook(code: String): Book {
        return bookDAO.getBook(code).toDomain()
    }


    override fun getCurrencies(): Flow<CurrenciesResult> = flow<CurrenciesResult> {
        val response  = apiClient.getCurrencies()
        val result = response.map{ currencyString ->
            currencyString.toCurrencyModel()
        }

        val responseStrings = response.joinToString(",")
        val currencyResponse = apiClient.getCurrency(responseStrings)
        currencyResponse.forEach { bookDTO ->
            val bookEntity = bookDTO?.toEntity()
            bookEntity?.let{ entity ->
                bookDAO.insert(bookEntity)
            }
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
