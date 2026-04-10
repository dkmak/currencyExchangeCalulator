package com.currencyexchangecalculator.domain

sealed interface Currency {
    val label: String
    val code: String

    data object USDC : Currency {
        override val label: String = "USDc"
        override val code: String = "USDC"
    }

    data object MXN : Currency {
        override val label: String = "MXN"
        override val code: String = "MXN"
    }

    data object ARS : Currency {
        override val label: String = "ARS"
        override val code: String = "ARS"
    }

    data object EURC : Currency {
        override val label: String = "EURc"
        override val code: String = "EURC"
    }

    data object COP : Currency {
        override val label: String = "COP"
        override val code: String = "COP"
    }

    data object BRL : Currency {
        override val label: String = "BRL"
        override val code: String = "BRL"
    }

    data class Unknown(
        override val label: String,
        override val code: String
    ) : Currency
}
