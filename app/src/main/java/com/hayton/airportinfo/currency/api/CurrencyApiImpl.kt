package com.hayton.airportinfo.currency.api

import com.hayton.airportinfo.currency.data.CurrencyResponse
import com.hayton.airportinfo.currency.data.ExchangeRateResponse
import com.hayton.airportinfo.currency.retrofit.CurrencyService
import retrofit2.Response
import javax.inject.Inject

interface CurrencyApi {
    suspend fun getCurrencies(currencies: List<String>): Response<CurrencyResponse>
    suspend fun getExchangeRate(baseCurrency: String, currencies: List<String>): Response<ExchangeRateResponse>
}

class CurrencyApiImpl @Inject constructor(private val currencyService: CurrencyService): CurrencyApi {
    override suspend fun getCurrencies(currencies: List<String>): Response<CurrencyResponse> {
        val currenciesString =
            if (currencies.isNotEmpty()) {
                currencies.reduce { acc, s ->
                    "$acc,$s"
                }
            } else {
                ""
            }
        return currencyService.getCurrencies(currenciesString)
    }

    override suspend fun getExchangeRate(
        baseCurrency: String,
        currencies: List<String>
    ): Response<ExchangeRateResponse> {
        val currenciesString =
            if (currencies.isNotEmpty()) {
                currencies.reduce { acc, s ->
                    "$acc,$s"
                }
            } else {
                ""
            }
        return currencyService.getExchangeRate(baseCurrency, currenciesString)
    }
}