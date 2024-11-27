package com.hayton.airportinfo.flight.data

data class DepartureSchedule(
    val expectTime: String,
    val realTime: String,
    val airLineName: String,
    val airLineCode: String,
    val airLineLogo: String,
    val airLineUrl: String,
    val airLineNum: String,
    val goalAirportCode: String,
    val goalAirportName: String,
    val airPlaneType: String,
    val airBoardingGate: String,
    val airFlyStatus: String,
    val airFlyDelayCause: String
)
