package com.hayton.airportinfo.di

import android.util.Log
import com.google.gson.Gson
import com.hayton.airportinfo.currency.api.CurrencyApi
import com.hayton.airportinfo.currency.api.CurrencyApiImpl
import com.hayton.airportinfo.currency.repository.CurrencyRepository
import com.hayton.airportinfo.currency.repository.CurrencyRepositoryImpl
import com.hayton.airportinfo.currency.retrofit.CurrencyService
import com.hayton.airportinfo.flight.api.AirportInfoApi
import com.hayton.airportinfo.flight.api.AirportInfoApiImpl
import com.hayton.airportinfo.flight.retrofit.AirportInfoService
import com.hayton.airportinfo.flight.repository.AirportInfoRepository
import com.hayton.airportinfo.flight.repository.AirportInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT = 30

    @Provides
    @Singleton
    @Named("airport")
    fun provideRetrofitClient(): Retrofit = Retrofit.Builder()
        .baseUrl("https://www.kia.gov.tw/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun provideAirportInfoService(@Named("airport") retrofit: Retrofit): AirportInfoService =
        retrofit.create(AirportInfoService::class.java)

    @Provides
    fun provideAirportInfoApi(service: AirportInfoService): AirportInfoApi =
        AirportInfoApiImpl(service)

    @Provides
    @Singleton
    fun provideAirportInfoRepository(airportInfoApi: AirportInfoApi): AirportInfoRepository =
        AirportInfoRepositoryImpl(airportInfoApi)

    @Provides
    @Singleton
    @Named("currency")
    fun provideCurrencyRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("apiKey", "fca_live_2MuR4GVbvbhNOi3r17qaljzQC5m994YeHoKul1jc")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.freecurrencyapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    @Provides
    fun provideCurrencyService(@Named("currency") retrofit: Retrofit): CurrencyService =
        retrofit.create(CurrencyService::class.java)

    @Provides
    fun provideCurrencyApi(currencyService: CurrencyService): CurrencyApi =
        CurrencyApiImpl(currencyService)

    @Provides
    @Singleton
    fun provideCurrencyRepository(currencyApi: CurrencyApi): CurrencyRepository =
        CurrencyRepositoryImpl(currencyApi)
}