package com.currencyexchangecalculator.data

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCurrencies(): List<CurrencyDTO?> {
        return apiService.getCurrencies()
    }
}

interface ApiService {
    @GET("v1/tickers?currencies=MXN,ARS,EURC")
    suspend fun getCurrencies(
    ): List<CurrencyDTO?>
}

@Serializable
data class CurrencyDTO(
    val ask: String,
    val bid: String,
    val book: String,
    val date: String
)

