package com.hayton.airportinfo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.hayton.airportinfo.currency.ui.CurrencyScreen
import com.hayton.airportinfo.ui.BottomNavigationBar
import com.hayton.airportinfo.flight.ui.FlightSchedulesScreen
import com.hayton.airportinfo.ui.nav.BottomNavigation
import com.hayton.airportinfo.ui.theme.AirportInfoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            AirportInfoTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        NavHost(navController = navController, startDestination = BottomNavigation.FlightSchedule.route) {
                            composable(route = BottomNavigation.FlightSchedule.route) { FlightSchedulesScreen() }
                            navigation(route = BottomNavigation.ForeignExchange.route, startDestination = "currency") {
                                composable("currency") { CurrencyScreen() }
                            }

                        }
                    }


                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview() {
    AirportInfoTheme {
        Greeting("Android")
    }
}