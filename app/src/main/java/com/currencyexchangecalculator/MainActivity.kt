package com.currencyexchangecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme
import com.currencyexchangecalculator.presentation.ui.CurrencyExchangeCalculatorScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyExchangeCalculatorTheme {
                CurrencyExchangeCalculatorScreen()
            }
        }
    }
}

