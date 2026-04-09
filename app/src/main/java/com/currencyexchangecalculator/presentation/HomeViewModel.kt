package com.currencyexchangecalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrencyModel
import com.currencyexchangecalculator.domain.CurrencyRepository
import com.currencyexchangecalculator.domain.CurrencyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.math.RoundingMode
import javax.inject.Inject


data class HomeUiState(
    val exchangeFromUSDc: Boolean = true, // rememberSavable
    val usdcTextField: String = "",
    val currencyTextField: String = "",
    val dataState: HomeDataState = HomeDataState.Loading,
) {
    sealed interface HomeDataState {
        data class Success(val book: Book) : HomeDataState
        data class Failure(val message: String) : HomeDataState
        data object Loading : HomeDataState
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        updateCurrency(CurrencyModel.MXN)
    }

    fun updateCurrency(currency: CurrencyModel) {
        repository.getCurrency(currency.code)
            .onEach { result ->
                _uiState.update { currentState ->
                    val dataState = result.toDataState()
                    if (dataState is HomeUiState.HomeDataState.Success){
                        val book = dataState.book
                        val rate = if (currentState.exchangeFromUSDc) book.ask else {
                            book.bid
                        }
                        val start = "1"
                        currentState.copy(
                            dataState = dataState,
                            usdcTextField = "1",
                            currencyTextField = convertUsdcToCurrency(rate, start)
                        )
                    } else {
                        currentState.copy(
                            dataState = dataState,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUsdTextFieldChanged(value: String) {
        if (isValidInput(value)) {
            _uiState.update { currentState ->
                val book = (currentState.dataState as? HomeUiState.HomeDataState.Success)?.book
                val convertCurrency = if (book != null && value.isNotEmpty()) {
                    val rate = if (currentState.exchangeFromUSDc) book.ask else {
                        book.bid
                    }
                    convertUsdcToCurrency(rate, value)
                } else {
                    ""
                }
                currentState.copy(
                    usdcTextField = value,
                    currencyTextField = convertCurrency
                )
            }
        }
    }

    fun onCurrencyTextFieldChanged(value: String) {
        if (isValidInput(value)) {
            _uiState.update { currentState ->
                val book = (currentState.dataState as? HomeUiState.HomeDataState.Success)?.book
                val convertCurrency = if (book != null && value.isNotEmpty()) {
                    convertCurrencyToUsdc(book.ask, value)
                } else {
                    ""
                }
                currentState.copy(
                    usdcTextField = convertCurrency,
                    currencyTextField = value
                )
            }
        }
    }

    fun clearTextFieldValues() {
        _uiState.update { currentState ->
            currentState.copy(
                usdcTextField = "",
                currencyTextField = ""
            )
        }
    }

    fun updateConvertFromUSDc(){
        _uiState.update { currentState ->
            currentState.copy(
                exchangeFromUSDc = !currentState.exchangeFromUSDc
            )
        }
    }

    private fun convertUsdcToCurrency(price: String, value: String): String {
        return value.toBigDecimalOrNull()?.multiply(price.toBigDecimalOrNull())
            ?.setScale(2, RoundingMode.HALF_UP)
            ?.toString()
            .orEmpty()
    }

    private fun convertCurrencyToUsdc(price: String, value: String): String {
        return value.toBigDecimalOrNull()?.divide(price.toBigDecimalOrNull(), 2, RoundingMode.HALF_UP)
            ?.toString()
            .orEmpty()
    }

    private fun CurrencyResult.toDataState(): HomeUiState.HomeDataState {
        return when (this) {
            is CurrencyResult.CurrencySuccess -> HomeUiState.HomeDataState.Success(
                book = this.book
            )
            is CurrencyResult.CurrencyError.Backend -> HomeUiState.HomeDataState.Failure(
                message = this.toUserMessage()
            )
            is CurrencyResult.CurrencyError.Network -> HomeUiState.HomeDataState.Failure(
                message = this.toUserMessage()
            )
            is CurrencyResult.CurrencyError.Unknown -> HomeUiState.HomeDataState.Failure(
                message = this.toUserMessage()
            )
        }
    }

    private fun isValidInput(value: String): Boolean {
        if (value.isEmpty()) return true
        val parts = value.split('.')

        if (parts.size > 2) return false
        if (parts.any { part -> part.any { !it.isDigit() } }) return false

        return parts.size == 1 || parts[1].length <= 2
    }
}
