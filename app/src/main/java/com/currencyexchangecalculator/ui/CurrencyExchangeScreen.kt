package com.currencyexchangecalculator.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.currencyexchangecalculator.domain.CurrencyModel
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme


@Composable
fun CurrencyExchangeCalculatorScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val dataState = uiState.dataState) {
                is HomeUiState.CurrencyDataState.Failure -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) { Text(text = dataState.message) }
                }

                HomeUiState.CurrencyDataState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator() }
                }

                is HomeUiState.CurrencyDataState.Success -> {
                    ExchangeCalculator(
                        book = dataState.book,
                        usdTextField = uiState.usdcTextField,
                        currencyTextField = uiState.currencyTextField,
                        exchangeFromUSD = uiState.exchangeFromUSDc,
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
                        onChangeCurrency = {
                            showBottomSheet = true
                        },
                        onSwapConversion = { viewModel.updateConvertFromUSDc() },
                        modifier = Modifier
                            .padding(
                                vertical = 100.dp,
                                horizontal = 16.dp
                            )
                            .fillMaxWidth()
                    )
                    if (showBottomSheet) {
                        CurrencyExchangeBottomSheet(
                            state = uiState.availableCurrenciesState,
                            onDismissRequest = { showBottomSheet = false }
                        )
                    }
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
    exchangeFromUSD: Boolean,
    onUsdTextFieldChanged: (String) -> Unit,
    onCurrencyTextFieldChanged: (String) -> Unit,
    onClickTextField: () -> Unit,
    onChangeCurrency: () -> Unit,
    onSwapConversion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(
            text = "Exchange calculator",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 30.sp
            )
        )
        val subtitle = if (exchangeFromUSD) {
            "${book.baseCurrency.label} = ${trimZeros(book.ask)} ${book.exchangeCurrency.label}"
        } else {
            "${book.baseCurrency.label} = ${trimZeros(book.bid)} ${book.exchangeCurrency.label}"
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
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                if (exchangeFromUSD) {
                    CurrencyItem(
                        label = book.baseCurrency.label,
                        textFieldValue = usdTextField,
                        onCurrencyTextFieldChanged = onUsdTextFieldChanged,
                        onClick = {},
                        onClickTextField = onClickTextField
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyItem(
                        label = book.exchangeCurrency.label,
                        textFieldValue = currencyTextField,
                        onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
                        onClick = { onChangeCurrency() },
                        onClickTextField = onClickTextField,
                    )
                } else {
                    CurrencyItem(
                        label = book.exchangeCurrency.label,
                        textFieldValue = currencyTextField,
                        onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
                        onClick = { onChangeCurrency() },
                        onClickTextField = onClickTextField,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyItem(
                        label = book.baseCurrency.label,
                        textFieldValue = usdTextField,
                        onCurrencyTextFieldChanged = onUsdTextFieldChanged,
                        onClick = {},
                        onClickTextField = onClickTextField
                    )
                }
            }

            IconButton(
                onClick = { onSwapConversion() },
                modifier = Modifier
                    .align(Alignment.Center)

            ) {
                Icon(
                    painterResource(R.drawable.arrow_arq),
                    contentDescription = "Swap Conversion",
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
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
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
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onClickTextField()
                        }
                    },
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
                date = "2026-04-08",
                baseCurrency = CurrencyModel.USDC,
                exchangeCurrency = CurrencyModel.MXN
            ),
            usdTextField = "1",
            currencyTextField = "18.42",
            exchangeFromUSD = true,
            onUsdTextFieldChanged = {},
            onCurrencyTextFieldChanged = {},
            onClickTextField = {},
            onChangeCurrency = {},
            onSwapConversion = {},
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}

private fun trimZeros(number: String): String? {
    return number.toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString()
}
