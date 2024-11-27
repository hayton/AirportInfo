package com.hayton.airportinfo.flight.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayton.airportinfo.flight.data.ArrivalSchedule
import com.hayton.airportinfo.flight.data.DepartureSchedule
import com.hayton.airportinfo.flight.repository.AirportInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FlightSchedulesScreenViewModel @Inject constructor(
    private val repository: AirportInfoRepository
): ViewModel() {

    private val _arrivalFlightSchedulesFlow = MutableStateFlow<List<ArrivalSchedule>>(emptyList())
    val arrivalFlightSchedulesFlow = _arrivalFlightSchedulesFlow.asStateFlow()

    private val _departureFlightSchedulesFlow = MutableStateFlow<List<DepartureSchedule>>(emptyList())
    val departureFlightSchedulesFlow = _departureFlightSchedulesFlow.asStateFlow()

    private val _isLoadingArrivalFlight = MutableStateFlow(false)
    val isLoadingArrivalFlight = _isLoadingArrivalFlight.asStateFlow()

    private val _isLoadingDepartureFlight = MutableStateFlow(false)
    val isLoadingDepartureFlight = _isLoadingDepartureFlight.asStateFlow()

    fun getArrivalFlightSchedules() {
        Log.d("FlightSchedulesScreenViewModel", "getArrival")
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingArrivalFlight.value = true
            val response = repository.getDomesticArrivalFlightSchedules()
            if (response.isSuccessful) {
                _arrivalFlightSchedulesFlow.value = response.body()?.InstantSchedule ?: emptyList()
            } else {
                _arrivalFlightSchedulesFlow.value = emptyList()
            }
            delay(1000)
            _isLoadingArrivalFlight.value = false
        }
    }

    fun getDepartureFlightSchedules() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingDepartureFlight.value = true
            val response = repository.getDomesticDepartureFlightSchedules()
            if (response.isSuccessful) {
                _departureFlightSchedulesFlow.value = response.body()?.InstantSchedule ?: emptyList()
            } else {
                _departureFlightSchedulesFlow.value = emptyList()
            }
            delay(1000)
            _isLoadingDepartureFlight.value = false
        }
    }

}