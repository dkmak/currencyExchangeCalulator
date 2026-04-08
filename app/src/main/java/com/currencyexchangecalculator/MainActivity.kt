package com.currencyexchangecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyExchangeCalculatorTheme {
                CurrencyExchangeCalculatorApp()
            }
        }
    }
}

@Composable
fun CurrencyExchangeCalculatorApp(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var exchangeToUSD by rememberSaveable { mutableStateOf(false) }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = uiState) {
                is HomeUiState.Failure -> {
                    Row(modifier = Modifier.fillMaxWidth()) { Text(text = state.message) }
                }

                HomeUiState.Loading -> {
                    Row(modifier = Modifier.fillMaxWidth()) { CircularProgressIndicator() }
                }

                is HomeUiState.Success -> {
                    ExchangeCalculator(
                        book = state.books.first(),
                        exchangeToUSD = exchangeToUSD,
                        onChangeCurrency = {},
                        modifier = Modifier
                            .padding(
                                vertical = 100.dp,
                                horizontal = 16.dp
                            )
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeCalculator(
    book: Book,
    exchangeToUSD: Boolean,
    onChangeCurrency: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = "Exchange calculator", style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "1 USDc",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

    }
}
