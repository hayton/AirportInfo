package com.hayton.airportinfo.currency.repository

import com.hayton.airportinfo.currency.api.CurrencyApi
import com.hayton.airportinfo.currency.data.CurrencyResponse
import com.hayton.airportinfo.currency.data.ExchangeRateResponse
import retrofit2.Response
import javax.inject.Inject

interface CurrencyRepository {
    suspend fun getCurrencies(currencies: List<String>): Response<CurrencyResponse>
    suspend fun getExchangeRate(baseCurrency: String, currencies: List<String>): Response<ExchangeRateResponse>
}

class CurrencyRepositoryImpl @Inject constructor(private val currencyApi: CurrencyApi): CurrencyRepository {
    override suspend fun getCurrencies(currencies: List<String>): Response<CurrencyResponse> {
        return currencyApi.getCurrencies(currencies)
    }

    override suspend fun getExchangeRate(
        baseCurrency: String,
        currencies: List<String>
    ): Response<ExchangeRateResponse> {
        return currencyApi.getExchangeRate(baseCurrency, currencies)
    }

}