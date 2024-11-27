package com.hayton.airportinfo.currency.retrofit

import com.hayton.airportinfo.currency.data.CurrencyResponse
import com.hayton.airportinfo.currency.data.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyService {
    @GET("latest")
    suspend fun getExchangeRate(
        @Query("base_currency") bastCurrency: String,
        @Query("currencies") currencies: String
    ): Response<ExchangeRateResponse>

    @GET("currencies")
    suspend fun getCurrencies(
        @Query("currencies") currencies: String
    ): Response<CurrencyResponse>

}