package com.currencyexchangecalculator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.currencyexchangecalculator.domain.CurrencyModel
import com.currencyexchangecalculator.presentation.HomeUiState.AvailableCurrenciesState
import com.currencyexchangecalculator.presentation.theme.CurrencyExchangeCalculatorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyExchangeBottomSheet(
    onDismissRequest: () -> Unit,
    state: AvailableCurrenciesState
) {
    ModalBottomSheet(
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
                    modifier = Modifier.fillMaxWidth()
                        .selectableGroup()
                ) {
                    state.currencies.forEach { currencyModel ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(56.dp)
                        ){
                            Text(
                                text = currencyModel.label,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                            Spacer(Modifier.weight(1f))
                            RadioButton(
                                selected = false,
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyExchangeBottomSheetPreview(){
    CurrencyExchangeCalculatorTheme {
        CurrencyExchangeBottomSheet(
            onDismissRequest = {},
            state = AvailableCurrenciesState.Success(
                currencies = listOf(
                    CurrencyModel.MXN,
                    CurrencyModel.ARS,
                    CurrencyModel.BRL,
                    CurrencyModel.COP,
                    CurrencyModel.EURC,
                )
            )
        )
    }
}
