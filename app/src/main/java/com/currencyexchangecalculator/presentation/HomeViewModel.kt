package com.currencyexchangecalculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.data.CurrencyRepository
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrencyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface HomeUiState {
    data class Success(val books: List<Book?>?): HomeUiState
    data class Failure(val message: String): HomeUiState
    data object Loading: HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CurrencyRepository
): ViewModel() {
    val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getCurrency()
    }

    fun getCurrency(){
        repository.getCurrency()
            .map { result  ->
                _uiState.update {result.toUiState()}
            }
            .catch { throwable ->
                _uiState.update { HomeUiState.Failure(throwable.message?:"An unknown error occurred.") }
            }
            .launchIn(viewModelScope)
    }

    private fun CurrencyResult.toUiState(): HomeUiState {
        return when (this){
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