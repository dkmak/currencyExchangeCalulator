package com.currencyexchangecalculator.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.currencyexchangecalculator.domain.Currency
import com.currencyexchangecalculator.presentation.HomeUiState.AvailableCurrenciesState
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeBottomSheet(
    onDismissRequest: () -> Unit,
    state: AvailableCurrenciesState,
    selected: Currency,
    onNewCurrencySelected: (Currency) -> Unit,
    sheetState: SheetState,
    preferredCurrency: Currency,
    onPreferredCurrencyClicked: (Currency) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val (selectedCurrency, onOptionSelected) = remember { mutableStateOf(selected) }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        when (state) {
            is AvailableCurrenciesState.Failure -> {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(state.message)
                }
            }

            is AvailableCurrenciesState.Loading -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AvailableCurrenciesState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    state.currencies.forEach { currencyModel ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (currencyModel == selectedCurrency),
                                    onClick = {
                                        onOptionSelected(currencyModel)
                                        coroutineScope.launch {
                                            sheetState.hide()
                                            onNewCurrencySelected(currencyModel)
                                        }.invokeOnCompletion {
                                            onDismissRequest()
                                        }
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(currencyModel.toDrawableResource()),
                                    contentDescription = "",
                                    modifier = Modifier.size(28.dp),
                                    tint = Color.Unspecified
                                )
                            }

                            Text(
                                text = currencyModel.label,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(Modifier.weight(1f))
                            CustomRadioButton(
                                selected = (currencyModel == selectedCurrency),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.tertiary,
                                    unselectedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            IconButton(
                                onClick = {
                                    onPreferredCurrencyClicked(currencyModel)
                                }
                            ) {
                                Icon(
                                    imageVector = if (currencyModel.code == preferredCurrency.code) Icons.Default.Favorite else {
                                        Icons.Default.FavoriteBorder
                                    },
                                    contentDescription = "Preferred Currency",
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun CurrencyExchangeBottomSheetLightPreview() {
    CurrencyExchangeCalculatorTheme(darkTheme = false) {
        CurrencyExchangeBottomSheet(
            onDismissRequest = {},
            selected = Currency.MXN,
            onNewCurrencySelected = {},
            state = AvailableCurrenciesState.Success(
                currencies = listOf(
                    Currency.MXN,
                    Currency.ARS,
                    Currency.BRL,
                    Currency.COP,
                    Currency.EURC,
                )
            ),
            sheetState = rememberModalBottomSheetState(),
            preferredCurrency = Currency.MXN,
            onPreferredCurrencyClicked = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF101412)
@Composable
private fun CurrencyExchangeBottomSheetDarkPreview() {
    CurrencyExchangeCalculatorTheme(darkTheme = true) {
        CurrencyExchangeBottomSheet(
            onDismissRequest = {},
            selected = Currency.MXN,
            onNewCurrencySelected = {},
            state = AvailableCurrenciesState.Success(
                currencies = listOf(
                    Currency.MXN,
                    Currency.ARS,
                    Currency.BRL,
                    Currency.COP,
                    Currency.EURC,
                )
            ),
            sheetState = rememberModalBottomSheetState(),
            preferredCurrency = Currency.MXN,
            onPreferredCurrencyClicked = {}
        )
    }
}
