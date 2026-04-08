package com.currencyexchangecalculator.domain

import coil.network.HttpException
import okio.IOException

fun Throwable.toCurrencyDomainError(): CurrencyResult {
    return when (this) {
        is IOException -> CurrencyResult.CurrencyError.Network
        is NoSuchElementException -> CurrencyResult.CurrencyError.Backend
        is HttpException -> CurrencyResult.CurrencyError.Backend
        else -> {
            CurrencyResult.CurrencyError.Unknown
        }
    }
}