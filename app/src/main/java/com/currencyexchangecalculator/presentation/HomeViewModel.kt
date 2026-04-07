package com.currencyexchangecalculator.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.currencyexchangecalculator.data.CurrencyDTO
import com.currencyexchangecalculator.data.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed interface HomeUiState {
    data class Success(val currency: List<CurrencyDTO?>?): HomeUiState
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
            .map { currencyDTOS ->
                _uiState.update { HomeUiState.Success(currencyDTOS) }
            }
            .catch { throwable ->
                _uiState.update { HomeUiState.Failure(throwable.message?:"An unknown error occurred.") }
            }
            .launchIn(viewModelScope)
    }
}