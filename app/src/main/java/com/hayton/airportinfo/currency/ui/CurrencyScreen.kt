package com.hayton.airportinfo.currency.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.Navigator
import androidx.navigation.navArgument
import com.hayton.airportinfo.common.CommonLoadingScreen
import com.hayton.airportinfo.currency.viewmodel.CurrencyScreenViewModel

@Composable
fun CurrencyScreen(
    viewModel: CurrencyScreenViewModel = hiltViewModel(),
) {
    val TAG = "CurrencyScreen"

    val exchangeRate by viewModel.exchangeRateItemObjectList.collectAsState()
    val isLoading by viewModel.isLoadingStateFlow.collectAsState()

    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(exchangeRate) { index, item ->
                ExchangeRateItemScreen(
                    index,
                    item,
                    onValueChange = { baseValue: String, baseCurrency: String ->
                        viewModel.calculateExchangeAmount(baseValue, baseCurrency)
                    }
                )
            }
        }

        if (isLoading) {
            CommonLoadingScreen()
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CurrencyScreenContentPreview() {

    val exchangeRate = listOf<Pair<String, Double>>(Pair("CAD", 1.1), Pair("EUR", 2.2), Pair("GBP", 3.3))

    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                text = "USD",
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.size(16.dp))
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(exchangeRate) {

                }
            }
        }

        CommonLoadingScreen()
    }

}