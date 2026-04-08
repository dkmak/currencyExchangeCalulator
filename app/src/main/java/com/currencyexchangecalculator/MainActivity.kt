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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme
import com.currencyexchangecalculator.ui.CurrencyVisualTransformation
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val dataState = uiState.dataState) {
                is HomeUiState.HomeDataState.Failure -> {
                    Row(modifier = Modifier.fillMaxWidth()) { Text(text = dataState.message) }
                }

                HomeUiState.HomeDataState.Loading -> {
                    Row(modifier = Modifier.fillMaxWidth()) { CircularProgressIndicator() }
                }

                is HomeUiState.HomeDataState.Success -> {
                    ExchangeCalculator(
                        book = dataState.book,
                        usdTextField = uiState.usdTextField,
                        currencyTextField = uiState.currencyTextField,
                        exchangeToUSD = uiState.convertFromUSDc,
                        onUsdTextFieldChanged = { newValue ->
                            viewModel.onUsdTextFieldChanged(
                                newValue
                            )
                        },
                        onCurrencyTextFieldChanged = { newValue ->
                            viewModel.onCurrencyTextFieldChanged(
                                newValue
                            )
                        },
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
    currencyTextField: String,
    exchangeToUSD: Boolean,
    onUsdTextFieldChanged: (String) -> Unit,
    onCurrencyTextFieldChanged: (String) -> Unit,
    onChangeCurrency: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = "Exchange calculator",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp
            )
        )
        val subtitle = if (exchangeToUSD) {
            "1 USDc = ${trimZeros(book.ask)} MXN"
        } else {
            "1 USDc = ${trimZeros(book.bid)} MXN"
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        CurrencyItem(
            label ="USDc",
            textFieldValue = usdTextField,
            onCurrencyTextFieldChanged = onUsdTextFieldChanged,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(16.dp))

        CurrencyItem(
            label = "MXN",
            textFieldValue = currencyTextField,
            onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CurrencyItem(
    label: String,
    textFieldValue: String,
    onCurrencyTextFieldChanged: (String) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))
        BasicTextField(
            value = textFieldValue,
            onValueChange = onCurrencyTextFieldChanged,
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(min = 40.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            visualTransformation = CurrencyVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.End
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyItemPreview() {
    CurrencyExchangeCalculatorTheme {
        CurrencyItem(
            label = "MXN",
            textFieldValue = "18.42",
            onCurrencyTextFieldChanged = {},
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}


private fun trimZeros(number: String): String? {
    return number.toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString()
}
