package com.hayton.airportinfo.flight.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hayton.airportinfo.common.LinearLoadingScreen
import com.hayton.airportinfo.flight.viewmodel.FlightSchedulesScreenViewModel
import kotlinx.coroutines.delay

@Composable
fun ArrivalSchedulesListScreen(
    viewModel: FlightSchedulesScreenViewModel = hiltViewModel()
) {
    val TAG = "SchedulesListScreen"

    LaunchedEffect(Unit) {
        while(true) {
            viewModel.getArrivalFlightSchedules()
            delay(10000)
        }
    }

    val arrivalSchedules by viewModel.arrivalFlightSchedulesFlow.collectAsState()
    val isLoading by viewModel.isLoadingArrivalFlight.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        LinearLoadingScreen(isLoading = isLoading)
        if (arrivalSchedules.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(arrivalSchedules) { flightSchedule ->
                    ArrivalScheduleItemScreen(
                        flightSchedule
                    )
                }
            }
        } else if (!isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No Arriving Flights")
            }
        }
    }

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SchedulesListScreenContentPreview() {

}