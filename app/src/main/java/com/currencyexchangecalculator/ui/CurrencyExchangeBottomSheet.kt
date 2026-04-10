package com.currencyexchangecalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.currencyexchangecalculator.domain.CurrencyModel
import com.currencyexchangecalculator.presentation.HomeUiState.AvailableCurrenciesState
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeBottomSheet(
    onDismissRequest: () -> Unit,
    state: AvailableCurrenciesState,
    selected: CurrencyModel,
    onNewCurrencySelected: (CurrencyModel) -> Unit,
    sheetState: SheetState
) {
    val coroutineScope = rememberCoroutineScope() // move this out later
    val (selectedCurrency, onOptionSelected) = remember { mutableStateOf(selected) }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
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
                                        }.invokeOnCompletion {
                                            onNewCurrencySelected(currencyModel)
                                            onDismissRequest()
                                        }

                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currencyModel.label,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Spacer(Modifier.weight(1f))
                            RadioButton(
                                selected = (currencyModel == selectedCurrency),
                                onClick = null
                            )
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
private fun CurrencyExchangeBottomSheetPreview() {
    CurrencyExchangeCalculatorTheme {
        CurrencyExchangeBottomSheet(
            onDismissRequest = {},
            selected = CurrencyModel.MXN,
            onNewCurrencySelected = {},
            state = AvailableCurrenciesState.Success(
                currencies = listOf(
                    CurrencyModel.MXN,
                    CurrencyModel.ARS,
                    CurrencyModel.BRL,
                    CurrencyModel.COP,
                    CurrencyModel.EURC,
                )
            ),
            sheetState = rememberModalBottomSheetState()
        )
    }
}
