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
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

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
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Loading,
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty USDc input clears converted currency value`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `empty exchange currency input clears USDc value`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onCurrencyTextFieldChanged("")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `entering one USDc converts to the selected currency using ask price`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            homeViewModel.onUsdTextFieldChanged("1")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `many sequential USDc input updates keep uiState in sync with latest value`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            homeViewModel.onUsdTextFieldChanged("1")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            homeViewModel.onUsdTextFieldChanged("12")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "12",
                    currencyTextField = "207.63",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `boundary input with two decimal places is accepted for USDc conversion`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("1.23")
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1.23",
                    currencyTextField = "21.28",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `boundary input with more than two decimal places is ignored for USDc conversion`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("1.234")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `boundary input containing multiple decimal separators is ignored`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.onUsdTextFieldChanged("1.234.5")
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggling exchange direction updates exchangeFromUSDc flag and updates calculations relative to the USDc value`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            homeViewModel.updateConvertFromUSDc()
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = false,
                    usdcTextField = "1",
                    currencyTextField = "17.31",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            homeViewModel.updateConvertFromUSDc()
            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `exception currency quote failure updates dataState with failure`() = runTest {
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencyError.Network
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(HomeUiState())

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Loading,
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Failure(
                        "Please check your internet connection and try again."
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `exception available currencies failure updates availableCurrenciesState with failure`() = runTest {
        val expectedBook = baseBook

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesError.Network
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            assertThat(awaitItem()).isEqualTo(HomeUiState())

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "",
                    currencyTextField = "",
                    dataState = HomeUiState.CurrencyDataState.Loading,
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Failure(
                        "Please check your internet connection and try again."
                    )
                )
            )

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Failure(
                        "Please check your internet connection and try again."
                    )
                )
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCurrency requests the selected currency code from repository`() = runTest {
        val expectedBook = baseBook
        val expectedCurrencies = baseCurrencies

        currencyRepository = FakeCurrencyRepository().apply{
            currencyResult = CurrencyResult.CurrencySuccess(expectedBook)
            currenciesResult = CurrenciesResult.CurrenciesSuccess(expectedCurrencies)
        }

        homeViewModel = HomeViewModel(
            repository = currencyRepository
        )

        homeViewModel.uiState.test {
            awaitItem() // initial state
            awaitItem() // load available currencies

            assertThat(awaitItem()).isEqualTo(
                HomeUiState(
                    isUsdCToSelectedCurrency = true,
                    usdcTextField = "1",
                    currencyTextField = "17.30",
                    dataState = HomeUiState.CurrencyDataState.Success(
                        expectedBook
                    ),
                    availableCurrenciesState = HomeUiState.AvailableCurrenciesState.Success(expectedCurrencies)
                )
            )
            homeViewModel.updateCurrency(Currency.EURC)

            assertThat(currencyRepository.lastCode).isEqualTo(Currency.EURC.code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    companion object {
        val baseBook = Book(
            ask = "17.30744",
            bid = "17.3029",
            baseCurrency = Currency.USDC,
            exchangeCurrency = Currency.MXN,
            date = ""
        )

        val baseCurrencies = listOf(Currency.MXN, Currency.EURC)
    }
}
