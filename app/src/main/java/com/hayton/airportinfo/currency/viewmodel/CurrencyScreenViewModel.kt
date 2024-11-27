package com.hayton.airportinfo.currency.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayton.airportinfo.currency.data.Currency
import com.hayton.airportinfo.currency.data.ExchangeRateItemObject
import com.hayton.airportinfo.currency.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class CurrencyScreenViewModel @Inject constructor(
    private val repository: CurrencyRepository
): ViewModel() {

    private val _currenciesStateFlow = MutableStateFlow<List<Pair<String, Currency>>>(emptyList())
    val currenciesStateFlow = _currenciesStateFlow.asStateFlow()

    private val _exchangeRateStateFlow = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val exchangeRateStateFlow = _exchangeRateStateFlow.asStateFlow()

    private val _exchangeRateItemObjectList = MutableStateFlow<List<ExchangeRateItemObject>>(emptyList())
    val exchangeRateItemObjectList = _exchangeRateItemObjectList.asStateFlow()

    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    var retryCount = 0

    init {
        getCurrencies()
    }

    fun calculateExchangeAmount(baseValue: String, baseCurrency: String) {
        val baseRate = _exchangeRateStateFlow.value.find { it.first == baseCurrency }?.second

        baseRate?.let {
            _exchangeRateItemObjectList.value = _exchangeRateItemObjectList.value.map { itemObject ->
                val equivalentRate =
                    (_exchangeRateStateFlow.value.find { it.first == itemObject.symbol }?.second ?: 0.0) / baseRate

                val amount =
                    if (baseValue.isEmpty()) {
                        BigDecimal(0)
                    } else {
                        BigDecimal(baseValue.toDouble() * equivalentRate)
                            .setScale(2, RoundingMode.HALF_EVEN)
                    }
                if (itemObject.symbol == baseCurrency)
                    itemObject
                else
                    itemObject.copy(rate = amount.toString())
            }
        }
    }

    private fun getCurrencies(
        currencies: List<String> = listOf("USD", "EUR", "SGD", "JPY", "GBP", "CHF", "HKD")
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getCurrencies(currencies)
            if (response.isSuccessful) {
                val data = response.body()?.data?.toList() ?: emptyList()
                _currenciesStateFlow.value = data
                getExchangeRate("USD")
            } else {
                retryCount++
                if (retryCount < 3) {
                    getCurrencies()
                }
            }
        }
    }

    private fun getExchangeRate(
        baseCurrency: String,
        currencies: List<String> = listOf("USD", "EUR", "SGD", "JPY", "GBP", "CHF", "HKD")
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getExchangeRate(baseCurrency, currencies)
            _isLoadingStateFlow.value = false
            if (response.isSuccessful) {
                val data = response.body()?.data?.toList() ?: emptyList()
                _exchangeRateStateFlow.value = data

                Log.d("CurrencyScreenViewModel", "data= ${data}")

                _exchangeRateItemObjectList.value = data.map { exchangeRate ->
                    ExchangeRateItemObject(
                        symbol = exchangeRate.first,
                        rate = BigDecimal(exchangeRate.second).setScale(2, RoundingMode.HALF_EVEN).toPlainString(),
                        fullName = _currenciesStateFlow.value.find { it.first  == exchangeRate.first}?.second?.name ?: ""
                    )
                }.toMutableList().let {
                    val usdExchangeRateItemObject = it.find { item -> item.symbol == "USD" }

                    usdExchangeRateItemObject?.apply {
                        it.removeIf { item -> item.symbol == "USD" }
                        it.add(0, this)
                    }
                    it
                }
            } else {
                _exchangeRateStateFlow.value = emptyList()
            }
        }
    }

}