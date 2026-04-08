package com.currencyexchangecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val usdTextField by viewModel.textField.collectAsStateWithLifecycle()

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
                        usdTextField = usdTextField,
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
    usdTextField: String,
    exchangeToUSD: Boolean,
    onChangeCurrency: () -> Unit,
    modifier: Modifier = Modifier
) {
    // if I do it like this, then my conversion logic will have to be done outside of the VM
    Column(modifier) {
        Text(
            text = "Exchange calculator",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp
            )
        )
        Text(
            text = "1 USDc",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "USDc",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            TextField(
                value = usdTextField,
                onValueChange = {  },
                modifier = Modifier.width(140.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MXN",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))

            TextField(
                value = usdTextField,
                onValueChange = {  },
                modifier = Modifier.width(140.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.End
                )
            )
        }





    }
}
