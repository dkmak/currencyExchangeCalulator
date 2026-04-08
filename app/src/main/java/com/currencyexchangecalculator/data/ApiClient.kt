package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.dto.BookDTO
import retrofit2.http.GET
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getBooks(): List<BookDTO?> {
        return apiService.getCurrencies()
    }
}

interface ApiService {
    @GET("v1/tickers?currencies=MXN")
    suspend fun getCurrencies(): List<BookDTO?>
}
