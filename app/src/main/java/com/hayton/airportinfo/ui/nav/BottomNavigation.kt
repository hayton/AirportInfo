package com.hayton.airportinfo.ui.nav

import com.hayton.airportinfo.R
import kotlinx.serialization.*

@Serializable
object FlightSchedule
@Serializable
object ForeignExchange

sealed class BottomNavigation(val route: String, val label: String, val icon: Int) {
    data object FlightSchedule: BottomNavigation("flightSchedule", "Flight Schedule", R.drawable.baseline_airplanemode_active_24)
    data object ForeignExchange: BottomNavigation("foreignExchange", "Foreign Exchange", R.drawable.baseline_attach_money_24)
}