package com.currencyexchangecalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.data.DataStoreRepository
import com.currencyexchangecalculator.data.dto.toCurrencyModel
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.Currency
import com.currencyexchangecalculator.domain.CurrencyRepository
import com.currencyexchangecalculator.domain.CurrencyResult
import com.currencyexchangecalculator.presentation.HomeViewModel.Companion.DEFAULT_START_EXCHANGE_CURRENCY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.RoundingMode
import javax.inject.Inject


data class HomeUiState(
    val isUsdCToSelectedCurrency: Boolean = true,
    val usdcTextField: String = "",
    val currencyTextField: String = "",
    val preferredCurrency: Currency = DEFAULT_START_EXCHANGE_CURRENCY,
    val dataState: CurrencyDataState = CurrencyDataState.Loading,
    val availableCurrenciesState: AvailableCurrenciesState = AvailableCurrenciesState.Loading
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
    private val repository: CurrencyRepository,
    private val preferencesRepository: DataStoreRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCurrencies()
        preferencesRepository.preferredCurrencyCode.onEach { savedCode ->
            _uiState.update { currentState ->
                currentState.copy(
                    preferredCurrency = savedCode.toCurrencyModel()
                )
            }

            updateCurrency(savedCode.toCurrencyModel())
        }.launchIn(viewModelScope)
    }

    fun updateCurrency(currency: Currency) {
        repository.getCurrency(currency.code)
            .onEach { result ->
                _uiState.update { currentState ->
                    val dataState = result.toCurrencyDataState()
                    if (dataState is HomeUiState.CurrencyDataState.Success) {
                        val book = dataState.book
                        val (usdcTextField, currencyTextField) =
                            recalculateForCurrentDirection(
                                book = book,
                                isUsdCToSelectedCurrency = currentState.isUsdCToSelectedCurrency,
                                usdcTextField = currentState.usdcTextField,
                                currencyTextField = currentState.currencyTextField
                            )
                        currentState.copy(
                            dataState = dataState,
                            usdcTextField = usdcTextField,
                            currencyTextField = currencyTextField
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

    private fun getCurrencies() {
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
                    val rate = if (currentState.isUsdCToSelectedCurrency) book.bid else {
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
                    val rate = if (currentState.isUsdCToSelectedCurrency) book.ask else {
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

    fun updateConvertFromUSDc() {
        _uiState.update { currentState ->
            val newExchangeFromUSDc = !currentState.isUsdCToSelectedCurrency
            val book = (currentState.dataState as? HomeUiState.CurrencyDataState.Success)?.book

            if (book != null) {
                val (usdcTextField, currencyTextField) = if (newExchangeFromUSDc) {
                    calculateFromUsdc(
                        book = book,
                        usdcValue = currentState.usdcTextField
                    )
                } else {
                    calculateFromSelectedCurrency(
                        book = book,
                        currencyValue = currentState.currencyTextField
                    )
                }

                currentState.copy(
                    isUsdCToSelectedCurrency = newExchangeFromUSDc,
                    usdcTextField = usdcTextField,
                    currencyTextField = currencyTextField
                )
            } else {
                currentState.copy(
                    isUsdCToSelectedCurrency = newExchangeFromUSDc
                )
            }
        }
    }

    fun updateSavedCurrencyPreferences(currency: Currency){
        viewModelScope.launch {
            preferencesRepository.savePreferredCurrency(currency.code)

            _uiState.update { currentState ->
                currentState.copy(
                    preferredCurrency = currency
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
        return value.toBigDecimalOrNull()
            ?.divide(price.toBigDecimalOrNull(), 2, RoundingMode.HALF_UP)
            ?.toString()
            .orEmpty()
    }

    private fun recalculateForCurrentDirection(
        book: Book,
        isUsdCToSelectedCurrency: Boolean,
        usdcTextField: String,
        currencyTextField: String
    ): Pair<String, String> {
        return if (isUsdCToSelectedCurrency) {
            calculateFromUsdc(
                book = book,
                usdcValue = usdcTextField.ifBlank { DEFAULT_BASE_CURRENCY_VALUE }
            )
        } else {
            calculateFromSelectedCurrency(
                book = book,
                currencyValue = currencyTextField.ifBlank { DEFAULT_BASE_CURRENCY_VALUE }
            )
        }
    }

    private fun calculateFromUsdc(book: Book, usdcValue: String): Pair<String, String> {
        if (usdcValue.isEmpty()) return "" to ""

        return usdcValue to convertUsdcToCurrency(
            price = book.bid,
            value = usdcValue
        )
    }

    private fun calculateFromSelectedCurrency(book: Book, currencyValue: String): Pair<String, String> {
        if (currencyValue.isEmpty()) return "" to ""

        return convertCurrencyToUsdc(
            price = book.ask,
            value = currencyValue
        ) to currencyValue
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
