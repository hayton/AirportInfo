package com.hayton.airportinfo.flight.api

import com.hayton.airportinfo.flight.retrofit.AirportInfoService
import com.hayton.airportinfo.flight.data.ArrivalInstantScheduleResponse
import com.hayton.airportinfo.flight.data.DepartureInstantScheduleResponse
import retrofit2.Response
import javax.inject.Inject


interface AirportInfoApi {
    suspend fun getDomesticArrivalFlightSchedules(): Response<ArrivalInstantScheduleResponse>
    suspend fun getDomesticDepartureFlightSchedules(): Response<DepartureInstantScheduleResponse>
}

class AirportInfoApiImpl @Inject constructor(
    private val airportInfoService: AirportInfoService
): AirportInfoApi {

    override suspend fun getDomesticArrivalFlightSchedules(): Response<ArrivalInstantScheduleResponse> {
        return airportInfoService.getDomesticArrivalFlightSchedules()
    }

    override suspend fun getDomesticDepartureFlightSchedules(): Response<DepartureInstantScheduleResponse> {
        return airportInfoService.getDomesticDepartureFlightSchedules()
    }
}