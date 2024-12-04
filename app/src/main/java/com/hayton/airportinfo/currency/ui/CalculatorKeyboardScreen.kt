package com.hayton.airportinfo.currency.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CalculatorKeyboardScreen(
    modifier: Modifier = Modifier,
    onKey: (keyLabel: String) -> Unit
) {
    val TAG = "CalculatorKeyboardScreen"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val row0 = listOf("C", "⌫")
        val row1 = listOf("7", "8", "9", "÷")
        val row2 = listOf("4", "5", "6", "×")
        val row3 = listOf("1", "2", "3", "-")
        val row4 = listOf("0", ".", "=", "+")

        Row {
            row0.forEach {
                Button(
                    onClick = {
                        Log.d(TAG, "value= $it")
                        onKey(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(2.8f)
                ) {
                    Text(text = it)
                }
            }
        }
        Row {
            row1.forEach {
                Button(
                    onClick = {
                        Log.d(TAG, "value= $it")
                        onKey(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                ) {
                    Text(text = it)
                }
            }
        }
        Row {
            row2.forEach {
                Button(
                    onClick = {
                        Log.d(TAG, "value= $it")
                        onKey(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                ) {
                    Text(text = it)
                }
            }
        }
        Row {
            row3.forEach {
                Button(
                    onClick = {
                        Log.d(TAG, "value= $it")
                        onKey(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                ) {
                    Text(text = it)
                }
            }
        }
        Row {
            row4.forEach {
                Button(
                    onClick = {
                        Log.d(TAG, "value= $it")
                        onKey(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1.4f)
                ) {
                    Text(text = it)
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CalculatorKeyboardScreenContentPreview() {

}