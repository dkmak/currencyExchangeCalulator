package com.currencyexchangecalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.data.CurrencyRepository
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrencyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import javax.inject.Inject


data class HomeUiState(
    val convertFromUSDc: Boolean = true,
    val usdTextField: String = "",
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
        getCurrency()
    }

    fun getCurrency() {
        repository.getCurrency()
            .onEach { result ->
                _uiState.update { currentState ->
                    val dataState = result.toDataState()
                    currentState.copy(
                        usdTextField = "1",
                        currencyTextField = "",
                        dataState = dataState,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onUsdTextFieldChanged(value: String) {
        if (value.isEmpty() || value.all { character -> character.isDigit() }) {
            _uiState.update { currentState ->
                currentState.copy(usdTextField = value)
            }
        }
    }

    fun onCurrencyTextFieldChanged(value: String) {
        if (value.isEmpty() || value.all { character -> character.isDigit() }) {
            _uiState.update { currentState ->
                currentState.copy(currencyTextField = value)
            }
        }
    }

    private fun convertCurrency(price: BigDecimal, value: BigDecimal): String? {
        val dataState = uiState.value.dataState as? HomeUiState.HomeDataState.Success
        dataState?.book?.let{ book ->
            val askPrice = book.ask

        }
        return ""
    }

    private fun CurrencyResult.toDataState(): HomeUiState.HomeDataState {
        return when (this) {
            is CurrencyResult.CurrencySuccess -> HomeUiState.HomeDataState.Success(book = this.book)
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
}
