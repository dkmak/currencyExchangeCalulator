package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.database.BookDAO
import com.currencyexchangecalculator.data.database.BookEntity
import com.currencyexchangecalculator.data.database.toBook
import com.currencyexchangecalculator.data.dto.BookDTO
import com.currencyexchangecalculator.data.dto.toCurrencyModel
import com.currencyexchangecalculator.data.dto.toDomain
import com.currencyexchangecalculator.data.dto.toEntity
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/*
* Database - Book
* - build a Room database
* - update repository
* -
* */

class CurrencyRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val bookDAO: BookDAO,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): CurrencyRepository {

/*    override fun getCurrency(code: String): Flow<CurrencyResult> = flow<CurrencyResult> {
        try {
            val response: List<BookDTO?> = apiClient.getCurrency(code)
            val bookResult = response.mapNotNull { dto ->
                dto?.toEntity()
            }.first()
            bookDAO.insertBook(bookResult)
            emit(CurrencyResult.CurrencySuccess(bookResult.toBook()))
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }
            val error = throwable.toCurrencyDomainError() as CurrencyResult.CurrencyError

            val cachedResult = getCurrencyEntity(code)?.toBook()
            if (cachedResult != null){
                emit(CurrencyResult.CurrencySuccess(
                    book = cachedResult,
                    warning = error,
                    isFromCache = true
                ))
            } else {
                emit(error)
            }
            return@flow
        }
        emit(throwable.toCurrencyDomainError())
    }.flowOn(ioDispatcher)*/

    override fun getCurrency(code: String): Flow<CurrencyResult> = flow<CurrencyResult> {
        try {
            val response: List<BookDTO?> = apiClient.getCurrency(code)
            val bookResult = response.mapNotNull { dto ->
                dto?.toEntity()
            }.first()
            bookDAO.insertBook(bookResult)
            emit(CurrencyResult.CurrencySuccess(getCurrencyEntity(code).toBook()))
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            }
            val cachedResult = getCurrencyEntity(code).toBook()
            emit(CurrencyResult.CurrencySuccess(cachedResult))
            // emit(throwable.toCurrencyDomainError())
            return@flow
        }
    }.flowOn(ioDispatcher)

    suspend fun getCurrencyEntity(code: String): BookEntity?{
        return bookDAO.getBookOrNull(code)
    }


    override fun getCurrencies(): Flow<CurrenciesResult> = flow<CurrenciesResult> {
        try {
            val response  = apiClient.getCurrencies()

            val currencyResponse = apiClient.getCurrency(response.joinToString(","))
            currencyResponse.forEach { dto->
                dto?.let{
                    bookDAO.insertBook(dto.toEntity())
                }
            }
        } catch (throwable: Throwable){
            if (throwable is CancellationException) {
                throw throwable
            }
            val cachedBooks = bookDAO.getAllBooks().map{ entity ->
                entity.exchangeCurrency.toCurrencyModel()
            }

            if (!cachedBooks.isEmpty()){
                emit(CurrenciesResult.CurrenciesSuccess(cachedBooks))
            } else {
                emit(throwable.toCurrenciesDomainError())
            }
            return@flow
        }

        val cachedBooks = bookDAO.getAllBooks().map{ entity ->
            entity.exchangeCurrency.toCurrencyModel()
        }

        emit(CurrenciesResult.CurrenciesSuccess(cachedBooks))
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
