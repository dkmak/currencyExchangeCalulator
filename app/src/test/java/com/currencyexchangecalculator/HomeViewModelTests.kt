package com.currencyexchangecalculator

import app.cash.turbine.test
import com.currencyexchangecalculator.domain.Book
import com.currencyexchangecalculator.domain.CurrenciesResult
import com.currencyexchangecalculator.domain.Currency
import com.currencyexchangecalculator.domain.CurrencyResult
import com.currencyexchangecalculator.presentation.HomeUiState
import com.currencyexchangecalculator.presentation.HomeViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class HomeViewModelTests {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var currencyRepository: FakeCurrencyRepository
    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup(){
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial getCurrencies and updateCurrency correctly updates uiState`() = runTest {
        val expectedBook = Book(
            ask = "17.30744",
            bid = "17.3029",
            baseCurrency = Currency.USDC,
            exchangeCurrency = Currency.MXN,
            date = ""
        )

        val expectedCurrencies = listOf(Currency.MXN)

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(HomeUiState())

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    exchangeFromUSDc = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Loading,
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    exchangeFromUSDc = true,
                    usdcTextField = "1",
                    currencyTextField = "17.31",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }




}
