@file:OptIn(ExperimentalPagerApi::class)

package com.hayton.airportinfo.flight.ui

import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LeadingIconTab
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.hayton.airportinfo.R
import com.hayton.airportinfo.flight.data.TabData
import kotlinx.coroutines.launch

@Composable
fun FlightSchedulesScreen() {
    val TAG = "FlightSchedulesScreen"

    val pagerState = rememberPagerState(pageCount = 2)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TabLayout(pagerState)
        TabContent(pagerState)
    }

}

@Composable
fun TabLayout(pagerState: PagerState = rememberPagerState(pageCount = 2)) {
    val scope = rememberCoroutineScope()

    val tabData = listOf(
        TabData("Arrival", R.drawable.baseline_flight_land_24),
        TabData("Departure", R.drawable.baseline_flight_takeoff_24)
    )
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            Spacer(modifier = Modifier.height(5.dp))
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier =
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 4.dp,
                color = colorResource(id = R.color.indicator)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabData.forEachIndexed { index, data ->
            LeadingIconTab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(text = data.tabName)
                },
                icon = {
                    Icon(painter = painterResource(id = data.tabIcon), contentDescription = "tab icon")
                },
            )
        }
    }
}

@Composable
fun TabContent(
    pagerState: PagerState = rememberPagerState(pageCount = 2),
) {
    HorizontalPager(state = pagerState) {
        if (it == 0) {
            ArrivalSchedulesListScreen()
        } else {
            DepartureSchedulesListScreen()
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun FlightSchedulesScreenContentPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TabLayout()
        TabContent()
    }

}