package com.hayton.airportinfo.flight.repository

import com.hayton.airportinfo.flight.api.AirportInfoApi
import com.hayton.airportinfo.flight.data.ArrivalInstantScheduleResponse
import com.hayton.airportinfo.flight.data.DepartureInstantScheduleResponse
import retrofit2.Response
import javax.inject.Inject


interface AirportInfoRepository {
    suspend fun getDomesticArrivalFlightSchedules(): Response<ArrivalInstantScheduleResponse>
    suspend fun getDomesticDepartureFlightSchedules(): Response<DepartureInstantScheduleResponse>
}

class AirportInfoRepositoryImpl @Inject constructor(
    private val airportInfoApi: AirportInfoApi
): AirportInfoRepository {

    override suspend fun getDomesticArrivalFlightSchedules(): Response<ArrivalInstantScheduleResponse> {
        return airportInfoApi.getDomesticArrivalFlightSchedules()
    }

    override suspend fun getDomesticDepartureFlightSchedules(): Response<DepartureInstantScheduleResponse> {
        return airportInfoApi.getDomesticDepartureFlightSchedules()
    }
}