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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.currencyexchangecalculator.domain.Currency
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeCalculatorScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
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
                            sheetState = sheetState,
                            state = uiState.availableCurrenciesState,
                            selected = dataState.book.exchangeCurrency,
                            onNewCurrencySelected = { currencyModel ->
                                viewModel.updateCurrency(currencyModel)
                            },
                            onDismissRequest = {
                                println("Bottom sheet dismissed")
                                showBottomSheet = false
                            }
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
            "1 ${book.baseCurrency.label} = ${trimZeros(book.bid)} ${book.exchangeCurrency.label}"
        } else {
            "1 ${book.baseCurrency.label} = ${trimZeros(book.ask)} ${book.exchangeCurrency.label}"
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
                        iconResource = book.baseCurrency.toDrawableResource(),
                        isClickable = false,
                        onCurrencyTextFieldChanged = onUsdTextFieldChanged,
                        onClick = {},
                        onClickTextField = onClickTextField
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyItem(
                        label = book.exchangeCurrency.label,
                        textFieldValue = currencyTextField,
                        iconResource = book.exchangeCurrency.toDrawableResource(),
                        isClickable = true,
                        onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
                        onClick = { onChangeCurrency() },
                        onClickTextField = onClickTextField,
                    )
                } else {
                    CurrencyItem(
                        label = book.exchangeCurrency.label,
                        textFieldValue = currencyTextField,
                        iconResource = book.exchangeCurrency.toDrawableResource(),
                        isClickable = true,
                        onCurrencyTextFieldChanged = onCurrencyTextFieldChanged,
                        onClick = { onChangeCurrency() },
                        onClickTextField = onClickTextField,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CurrencyItem(
                        label = book.baseCurrency.label,
                        textFieldValue = usdTextField,
                        iconResource = book.baseCurrency.toDrawableResource(),
                        isClickable = false,
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
                    painterResource(R.drawable.arrow),
                    contentDescription = "Swap Conversion",
                    modifier = Modifier.size(36.dp),
                    tint = Color.Unspecified,
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
    iconResource: Int,
    isClickable: Boolean,
    onClick: () -> Unit,
    onClickTextField: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemModifier = if (isClickable){
        Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(16.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = itemModifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painterResource(id = iconResource),
                contentDescription = "",
                modifier = Modifier.size(16.dp),
                tint = Color.Unspecified
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            if (isClickable){
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Unspecified
                )
            }

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
            iconResource = R.drawable.mxn_flag,
            isClickable = true,
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
                baseCurrency = Currency.USDC,
                exchangeCurrency = Currency.MXN
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

fun Currency.toDrawableResource(): Int {
    return when (this){
        Currency.ARS -> R.drawable.ars_flag
        Currency.BRL -> R.drawable.brl_flag
        Currency.COP -> R.drawable.cop_flag
        Currency.EURC -> R.drawable.eurc_flag
        Currency.MXN -> R.drawable.mxn_flag
        Currency.USDC -> R.drawable.usdc_flag
        is Currency.Unknown -> R.drawable.ic_launcher_foreground
    }
}
