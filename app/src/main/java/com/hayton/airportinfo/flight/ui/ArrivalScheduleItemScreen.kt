package com.hayton.airportinfo.flight.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hayton.airportinfo.common.flightStatusFormat
import com.hayton.airportinfo.flight.data.ArrivalSchedule

@Composable
fun ArrivalScheduleItemScreen(arrivalSchedule: ArrivalSchedule) {
    val TAG = "ScheduleItemScreen"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = arrivalSchedule.airLineLogo,
                    modifier = Modifier.fillMaxHeight(),
                    contentDescription = "airLineLogo")
                Spacer(modifier = Modifier.size(8.dp, 0.dp))
                Text(text = arrivalSchedule.airLineName, fontSize = 14.sp)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.wrapContentHeight().weight(1.3f)
                ) {
                    Text(text = "Expected", fontSize = 14.sp)
                    Text(text = arrivalSchedule.expectTime, fontSize = 14.sp)
                }

                Text(text = arrivalSchedule.upAirportName, modifier = Modifier.weight(0.7f), fontSize = 14.sp)

                Text(text = arrivalSchedule.airLineNum, modifier = Modifier.weight(1.3f), fontSize = 14.sp)

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(0.8f)
                ) {
                    Text(text = "Gate", fontSize = 14.sp)
                    Text(
                        text = arrivalSchedule.airBoardingGate.ifEmpty { "-" },
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = arrivalSchedule.airFlyStatus.flightStatusFormat(),
                    modifier = Modifier.weight(1.3f),
                    fontSize = 14.sp
                )

                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .weight(1f)
                ) {
                    Text(text = "Actual", fontSize = 14.sp)
                    Text(text = arrivalSchedule.realTime, fontSize = 14.sp)
                }            }
        }
    }

}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ScheduleItemScreenContentPreview() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.wrapContentHeight().weight(1f)
            ) {
                Text(text = "Expected", fontSize = 12.sp)
                Text(text = "09:50", fontSize = 16.sp)
            }

            Text(text = "Destination", modifier = Modifier.weight(1.2f))

            Text(text = "Flight", modifier = Modifier.weight(1f))

            Text(text = "Gate", modifier = Modifier.weight(1f))

            Text(text = "Status", modifier = Modifier.weight(1f))

            Text(text = "Time", modifier = Modifier.weight(1f))
        }
    }

}