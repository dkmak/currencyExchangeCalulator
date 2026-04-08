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
import javax.inject.Inject

sealed interface HomeUiState {
    data class Success(val books: List<Book>): HomeUiState
    data class Failure(val message: String) : HomeUiState
    data object Loading : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val _textField = MutableStateFlow("")
    val textField = _textField.asStateFlow()

    val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getCurrency()
    }

    fun getCurrency() {
        repository.getCurrency()
            .onEach { result ->
                _uiState.update { result.toUiState() }
            }
            .launchIn(viewModelScope)
    }
    

    private fun CurrencyResult.toUiState(): HomeUiState {
        return when (this) {
            is CurrencyResult.CurrencySuccess -> HomeUiState.Success(books = this.books)
            is CurrencyResult.CurrencyError.Backend -> HomeUiState.Failure(
                message = this.toUserMessage()
            )
            is CurrencyResult.CurrencyError.Network -> HomeUiState.Failure(
                message = this.toUserMessage()
            )
            is CurrencyResult.CurrencyError.Unknown -> HomeUiState.Failure(
                message = this.toUserMessage()
            )
        }
    }
}
