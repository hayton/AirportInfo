package com.hayton.airportinfo.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LinearLoadingScreen(
    isLoading: Boolean
) {
    val TAG = "LinearLoadingScreen"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
    ) {
        if (isLoading)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Blue,
                trackColor = Color.Transparent
            )
        else
            Spacer(
                modifier = Modifier
                    .height(4.dp)
                    .background(Color.Transparent)
            )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LinearLoadingScreenContentPreview() {

}