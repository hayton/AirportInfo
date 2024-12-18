package com.hayton.airportinfo

import MainDispatcherRule
import android.util.Log
import app.cash.turbine.test
import com.hayton.airportinfo.currency.data.Currency
import com.hayton.airportinfo.currency.data.CurrencyResponse
import com.hayton.airportinfo.currency.data.ExchangeRateResponse
import com.hayton.airportinfo.currency.repository.CurrencyRepository
import com.hayton.airportinfo.currency.viewmodel.CurrencyScreenViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import retrofit2.Response
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyScreenViewModelTest{

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: CurrencyRepository

    private lateinit var viewModel: CurrencyScreenViewModel

    @Before
    fun setup() {
        repository = mockk<CurrencyRepository>(relaxed = true)
        viewModel = spyk(
            CurrencyScreenViewModel(
                repository
            )
        )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `getCurrencies successfully updates currenciesStateFlow and calls getExchangeRate`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR", "SGD", "JPY", "GBP", "CHF", "HKD")
            val currencyData = currencies.associateWith {
                Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }
            val response = Response.success(CurrencyResponse(currencyData))
            coEvery { repository.getCurrencies(any()) } returns response

            // Mock getExchangeRate response
            val exchangeRateData = currencies.map {
                it to 1.0 // Simplified rate for testing
            }.toMap()
            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))
            coEvery { repository.getExchangeRate("USD",any()) } returns exchangeRateResponse

            viewModel.getCurrencies()

            viewModel.currenciesStateFlow.test {
                assertEquals(currencyData.toList(), viewModel.currenciesStateFlow.value)
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.exchangeRateStateFlow.test {
                assertEquals(exchangeRateData.toList(), viewModel.exchangeRateStateFlow.value)
                cancelAndIgnoreRemainingEvents()
            }


        }
    }

    @Test
    fun `getCurrencies fails and retries up to 3 times`() = runTest {
        // Given
        val currencies = listOf("USD", "EUR", "SGD", "JPY", "GBP", "CHF", "HKD")
        val errorResponse = Response.error<CurrencyResponse>(500, okhttp3.ResponseBody.create(null, ""))
        coEvery { repository.getCurrencies(currencies) } returns errorResponse

        viewModel.getCurrencies(currencies)

        // Then
        // Verify that getCurrencies was called 3 times
        coVerify(exactly = 3) { viewModel.getCurrencies(currencies) }

        // Verify that currenciesStateFlow remains empty
        assertTrue(viewModel.currenciesStateFlow.value.isEmpty())

        // Verify that exchangeRateStateFlow remains empty
        assertTrue(viewModel.exchangeRateStateFlow.value.isEmpty())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `clearFocusedItem clears the rate of the specified item`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR", "SGD")
            val currencyData = currencies.associateWith {
                Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }
            val response = Response.success(CurrencyResponse(currencyData))
            coEvery { repository.getCurrencies(any()) } returns response

            // Mock getExchangeRate response
            val exchangeRateData = mapOf("USD" to 1.0, "EUR" to 1.85, "SGD" to 1.35)

            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))
            coEvery { repository.getExchangeRate("USD",currencies) } returns exchangeRateResponse

            viewModel.getCurrencies(currencies)

            viewModel.exchangeRateItemObjectList.test {
                // When
                val focusedIndex = 1 // Index of EUR
                viewModel.clearFocusedItem(focusedIndex)

                // Then
                val updatedItems = viewModel.exchangeRateItemObjectList.value
                assertEquals("1.00", updatedItems[0].rate) // USD remains unchanged
                assertEquals("", updatedItems[1].rate)     // EUR rate cleared
                assertEquals("1.35", updatedItems[2].rate) // SGD remains unchanged

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `updateFocusedItem appends character when allowed`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR")
            val currencyData = currencies.associateWith {
                Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }
            val currencyResponse = Response.success(CurrencyResponse(currencyData))

            val exchangeRateData = mapOf("USD" to 1.0, "EUR" to 0.8)
            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))

            coEvery { repository.getCurrencies(any()) } returns (currencyResponse)
            coEvery { repository.getExchangeRate("USD", any()) } returns (exchangeRateResponse)

            viewModel.getCurrencies()

            viewModel.exchangeRateItemObjectList.test {
                // Ensure initial rate
                val focusedIndex = 1 // EUR
                val initialRate = viewModel.exchangeRateItemObjectList.value[focusedIndex].rate
                assertEquals("0.80", initialRate)

                // remove last char
                viewModel.removeLastChar(focusedIndex)
                val modifiedRate = viewModel.exchangeRateItemObjectList.value[focusedIndex].rate
                assertEquals("0.8", modifiedRate)

                // When
                val charToAdd = "2"
                viewModel.updateFocusedItem(focusedIndex, charToAdd)

                // Then
                val updatedRate = viewModel.exchangeRateItemObjectList.value[focusedIndex].rate
                assertEquals("0.82", updatedRate)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `updateFocusedItem does not append character when not allowed`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR")
            val currencyData = currencies.map {
                it to Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }.toMap()
            val currencyResponse = Response.success(CurrencyResponse(currencyData))

            val exchangeRateData = mapOf("USD" to 1.0, "EUR" to 0.85)
            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))

            coEvery { repository.getCurrencies(any()) } returns (currencyResponse)
            coEvery { repository.getExchangeRate("USD", any()) } returns (exchangeRateResponse)

            viewModel.getCurrencies()

            val focusedIndex = 1 // EUR

            viewModel.exchangeRateItemObjectList.test {
                // remove last character to enable editing
                viewModel.removeLastChar(focusedIndex)
                assertEquals("0.8", viewModel.exchangeRateItemObjectList.value[focusedIndex].rate)

                // try to append another decimal dot
                viewModel.updateFocusedItem(focusedIndex, ".")
                assertEquals("0.8", viewModel.exchangeRateItemObjectList.value[focusedIndex].rate)

                // ensure only 8 is appended, since adding 5 would exceed 2 decimal places
                viewModel.updateFocusedItem(focusedIndex, "8")
                viewModel.updateFocusedItem(focusedIndex, "5")
                assertEquals("0.88", expectMostRecentItem()[focusedIndex].rate)
            }
        }
    }

    @Test
    fun `removeLastChar removes the last character from the specified item`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR")
            val currencyData = currencies.map {
                it to Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }.toMap()
            val currencyResponse = Response.success(CurrencyResponse(currencyData))

            val exchangeRateData = mapOf("USD" to 1.0, "EUR" to 0.8)
            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))

            coEvery { repository.getCurrencies(any()) } returns (currencyResponse)
            coEvery { repository.getExchangeRate("USD", any()) } returns (exchangeRateResponse)

            viewModel.getCurrencies()

            viewModel.exchangeRateItemObjectList.test {
                // When
                val focusedIndex = 1 // EUR
                viewModel.updateFocusedItem(focusedIndex, "2") // Rate becomes "0.82"
                viewModel.removeLastChar(focusedIndex)

                // Then
                val updatedRate = viewModel.exchangeRateItemObjectList.value[focusedIndex].rate
                assertEquals("0.8", updatedRate)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

    @Test
    fun `calculateResult evaluates the expression and updates exchangeRateItemObjectList`() {
        runTest {
            // Given
            val currencies = listOf("USD", "EUR")
            val currencyData = currencies.map {
                it to Currency(
                    symbol = it,
                    name = "$it Name",
                    symbol_native = it,
                    decimal_digits = 2,
                    rounding = 0,
                    code = it,
                    name_plural = "${it} Names"
                )
            }.toMap()
            val currencyResponse = Response.success(CurrencyResponse(currencyData))

            val exchangeRateData = mapOf("USD" to 1.0, "EUR" to 0.85)
            val exchangeRateResponse = Response.success(ExchangeRateResponse(exchangeRateData))

            coEvery { repository.getCurrencies(any()) } returns (currencyResponse)
            coEvery { repository.getExchangeRate("USD", any()) } returns (exchangeRateResponse)

            viewModel.getCurrencies()

            viewModel.exchangeRateItemObjectList.test {
                // Ensure initial rate
                val focusedIndex = 1 // EUR
                viewModel.clearFocusedItem(focusedIndex)
                viewModel.updateFocusedItem(focusedIndex, "2")
                viewModel.updateFocusedItem(focusedIndex, "+")
                viewModel.updateFocusedItem(focusedIndex, "3")

                val expression = viewModel.exchangeRateItemObjectList.value[focusedIndex].rate
                assertEquals("2+3", expression)

                // When
                viewModel.calculateResult(focusedIndex)

                // Then
                val updatedItems = viewModel.exchangeRateItemObjectList.value
                // The expression "2+3" evaluates to 5.0
                // USD rate should be updated: (1.0 / baseRate) * 5.0
                // Since baseRate for EUR is 0.85, the calculation is (1.0 / 0.85) * 5.0 ≈ 5.88
                val usdRate =
                    BigDecimal((1.0 / 0.85) * 5.0).setScale(2, RoundingMode.HALF_EVEN).toString()
                assertEquals(usdRate, updatedItems[0].rate)

                // EUR rate should be updated to "5.00"
                assertEquals("5.00", updatedItems[1].rate)

                cancelAndIgnoreRemainingEvents()
            }
        }
    }

}