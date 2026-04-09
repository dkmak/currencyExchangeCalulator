package com.currencyexchangecalculator.data

import com.currencyexchangecalculator.data.dto.BookDTO
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCurrency(code: String): List<BookDTO?> {
        return apiService.getCurrency(listOf(code))
    }

    suspend fun getCurrencies(): List<String> {
        return listOf("ARS","EURC","COP","MXN","BRL")
    }
}

interface ApiService {
    @GET("v1/tickers")
    suspend fun getCurrency(
        @Query("currencies") currencies: List<String>
    ): List<BookDTO?>

    @GET("/v1/tickers-currencies")
    suspend fun getCurrencies(): List<String>
}