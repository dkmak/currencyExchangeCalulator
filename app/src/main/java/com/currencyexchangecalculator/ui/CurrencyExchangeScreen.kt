package com.currencyexchangecalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.currencyexchangecalculator.R
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme


@Composable
fun CurrencyExchangeCalculatorScreen(
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
                        usdTextField = uiState.usdcTextField,
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
                        onClickTextField = {
                            viewModel.clearTextFieldValues()
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
    onClickTextField: () -> Unit,
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

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                CurrencyItem(
                    label = "USDc",
                    textFieldValue = usdTextField,
                    onCurrencyTextFieldChanged = onUsdTextFieldChanged,
                    onClick = {},
                    onClickTextField = onClickTextField
                )
                Spacer(modifier = Modifier.height(16.dp))
                CurrencyItem(
                    label = "MXN",
                    textFieldValue = currencyTextField,
                    onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
                    onClick = {},
                    onClickTextField = onClickTextField,
                )

            }

            IconButton (
                onClick = {},
                modifier = Modifier
                    .align(Alignment.Center)

            ){
                Icon(
                    painterResource(R.drawable.arrow_arq),
                    contentDescription = "Swap Converstion",
                    tint = Color.Unspecified
                )
            }


        }



    }
}

@Composable
private fun CurrencyItem(
    label: String,
    textFieldValue: String,
    onCurrencyTextFieldChanged: (String) -> Unit,
    onClick: () -> Unit,
    onClickTextField: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(66.dp)
            .fillMaxWidth()
        ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.weight(1f))
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    onCurrencyTextFieldChanged(newValue)
                },
                modifier = Modifier
                    .wrapContentWidth()
                    .onFocusChanged{ focusState ->
                        if (focusState.isFocused) {
                            onClickTextField()
                        }
                    }
                ,
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
}

@Preview(showBackground = true, backgroundColor = 0xFFEFEFEC)
@Composable
private fun CurrencyItemPreview() {
    CurrencyExchangeCalculatorTheme {
        CurrencyItem(
            label = "MXN",
            textFieldValue = "18.42",
            onCurrencyTextFieldChanged = {},
            onClickTextField = {},
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEFEFEC)
@Composable
private fun ExchangeCalculatorPreview() {
    CurrencyExchangeCalculatorTheme {
        ExchangeCalculator(
            book = Book(
                ask = "18.42",
                bid = "18.31",
                book = "mxn_usdc",
                date = "2026-04-08"
            ),
            usdTextField = "1",
            currencyTextField = "18.42",
            exchangeToUSD = true,
            onUsdTextFieldChanged = {},
            onCurrencyTextFieldChanged = {},
            onClickTextField = {},
            onChangeCurrency = {},
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}

private fun trimZeros(number: String): String? {
    return number.toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString()
}
