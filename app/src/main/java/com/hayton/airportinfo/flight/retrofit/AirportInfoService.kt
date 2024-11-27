package com.hayton.airportinfo.flight.retrofit

import com.hayton.airportinfo.flight.data.ArrivalInstantScheduleResponse
import com.hayton.airportinfo.flight.data.DepartureInstantScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirportInfoService {
    @GET("API/InstantSchedule.ashx")
    suspend fun getDomesticArrivalFlightSchedules(
        @Query("AirFlyLine") airFlyLine: String = "2",
        @Query("AirFlyIO") airFlyIO: String = "2"
    ): Response<ArrivalInstantScheduleResponse>

    @GET("API/InstantSchedule.ashx")
    suspend fun getDomesticDepartureFlightSchedules(
        @Query("AirFlyLine") airFlyLine: String = "2",
        @Query("AirFlyIO") airFlyIO: String = "1"
    ): Response<DepartureInstantScheduleResponse>
}