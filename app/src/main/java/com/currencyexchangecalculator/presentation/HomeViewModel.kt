package com.currencyexchangecalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.Currency
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
    val exchangeFromUSDc: Boolean = true,
    val usdcTextField: String = "",
    val currencyTextField: String = "",
    val dataState: CurrencyDataState = CurrencyDataState.Loading,
    val availableCurrenciesState: AvailableCurrenciesState =  AvailableCurrenciesState.Loading
) {
    sealed interface CurrencyDataState {
        data class Success(val book: Book) : CurrencyDataState
        data class Failure(val message: String) : CurrencyDataState
        data object Loading : CurrencyDataState
    }

    sealed interface AvailableCurrenciesState {
        data object Loading : AvailableCurrenciesState
        data class Success(val currencies: List<Currency>) : AvailableCurrenciesState
        data class Failure(val message: String) : AvailableCurrenciesState
    }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCurrencies()
        updateCurrency(DEFAULT_START_EXCHANGE_CURRENCY)
    }

    fun updateCurrency(currency: Currency) {
        repository.getCurrency(currency.code)
            .onEach { result ->
                _uiState.update { currentState ->
                    val dataState = result.toCurrencyDataState()
                    if (dataState is HomeUiState.CurrencyDataState.Success){
                        val book = dataState.book
                        val rate = if (currentState.exchangeFromUSDc) book.bid else {
                            book.ask
                        }
                        currentState.copy(
                            dataState = dataState,
                            usdcTextField = DEFAULT_BASE_CURRENCY_VALUE,
                            currencyTextField = convertUsdcToCurrency(rate, DEFAULT_BASE_CURRENCY_VALUE)
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

    fun getCurrencies(){
        repository.getCurrencies()
            .onEach { result ->
                _uiState.update { currentState ->
                    val availableDataState = result.toAvailableCurrenciesState()
                    currentState.copy(
                        availableCurrenciesState = availableDataState
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun onUsdTextFieldChanged(value: String) {
        if (isValidInput(value)) {
            _uiState.update { currentState ->
                val book = (currentState.dataState as? HomeUiState.CurrencyDataState.Success)?.book
                val convertCurrency = if (book != null && value.isNotEmpty()) {
                    val rate = if (currentState.exchangeFromUSDc) book.bid else {
                        book.ask
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
                val book = (currentState.dataState as? HomeUiState.CurrencyDataState.Success)?.book
                val convertCurrency = if (book != null && value.isNotEmpty()) {
                    val rate = if (currentState.exchangeFromUSDc) book.ask else {
                        book.bid
                    }
                    convertCurrencyToUsdc(rate, value)
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
            val newExchangeFromUSDc = !currentState.exchangeFromUSDc
            val book = (currentState.dataState as? HomeUiState.CurrencyDataState.Success)?.book

            if (book != null) {
                val usdcValue = currentState.usdcTextField
                val recalculatedCurrencyValue = if (usdcValue.isNotEmpty()) {
                    val rate = if (newExchangeFromUSDc) book.bid else book.ask
                    convertUsdcToCurrency(rate, usdcValue)
                } else {
                    ""
                }

                currentState.copy(
                    exchangeFromUSDc = newExchangeFromUSDc,
                    currencyTextField = recalculatedCurrencyValue
                )
            } else {
                currentState.copy(
                    exchangeFromUSDc = newExchangeFromUSDc
                )
            }
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

    private fun CurrencyResult.toCurrencyDataState(): HomeUiState.CurrencyDataState {
        return when (this) {
            is CurrencyResult.CurrencySuccess -> HomeUiState.CurrencyDataState.Success(
                book = this.book
            )
            is CurrencyResult.CurrencyError.Backend -> HomeUiState.CurrencyDataState.Failure(
                message = this.currencyErrorToUserMessage()
            )
            is CurrencyResult.CurrencyError.Network -> HomeUiState.CurrencyDataState.Failure(
                message = this.currencyErrorToUserMessage()
            )
            is CurrencyResult.CurrencyError.Unknown -> HomeUiState.CurrencyDataState.Failure(
                message = this.currencyErrorToUserMessage()
            )
        }
    }

    private fun CurrenciesResult.toAvailableCurrenciesState(): HomeUiState.AvailableCurrenciesState {
        return when (this) {
            is CurrenciesResult.CurrenciesSuccess -> HomeUiState.AvailableCurrenciesState.Success(
                currencies = this.currencies
            )
            is CurrenciesResult.CurrenciesError.Backend -> HomeUiState.AvailableCurrenciesState.Failure(
                message = this.currenciesErrorToUserMessage()
            )
            is CurrenciesResult.CurrenciesError.Network -> HomeUiState.AvailableCurrenciesState.Failure(
                message = this.currenciesErrorToUserMessage()
            )
            is CurrenciesResult.CurrenciesError.Unknown -> HomeUiState.AvailableCurrenciesState.Failure(
                message = this.currenciesErrorToUserMessage()
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

    companion object {
        const val DEFAULT_BASE_CURRENCY_VALUE = "1"
        val DEFAULT_START_EXCHANGE_CURRENCY = Currency.MXN
    }
}
