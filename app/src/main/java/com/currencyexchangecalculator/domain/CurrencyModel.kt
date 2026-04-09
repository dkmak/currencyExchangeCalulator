package com.currencyexchangecalculator.domain

sealed interface CurrencyModel {
    val label: String
    val code: String

    data object USDC : CurrencyModel {
        override val label: String = "USDc"
        override val code: String = "USDC"
    }

    data object MXN : CurrencyModel {
        override val label: String = "MXN"
        override val code: String = "MXN"
    }

    data object ARS : CurrencyModel {
        override val label: String = "ARS"
        override val code: String = "ARS"
    }

    data object EURC : CurrencyModel {
        override val label: String = "EURc"
        override val code: String = "EURC"
    }

    data object COP : CurrencyModel {
        override val label: String = "COP"
        override val code: String = "COP"
    }

    data object BRL : CurrencyModel {
        override val label: String = "BRL"
        override val code: String = "BRL"
    }

    data class Unknown(
        override val label: String,
        override val code: String
    ) : CurrencyModel
}
