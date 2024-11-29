package com.hayton.airportinfo.currency.ui

import android.util.Log
import android.view.ViewTreeObserver
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hayton.airportinfo.currency.data.ExchangeRateItemObject

@Composable
fun ExchangeRateItemScreen(
    index: Int,
    exchangeRateItem: ExchangeRateItemObject,
    onValueChange: (value: String, baseCurrency: String) -> Unit
) {
    val TAG = "ExchangeRateItemScreen"

    var textState by remember { mutableStateOf(TextFieldValue(exchangeRateItem.rate)) }
    val focusRequester =  remember { FocusRequester() }

    val focusManager = LocalFocusManager.current

    val isKeyboardOpen by keyboardAsState()

    LaunchedEffect(isKeyboardOpen) {
        Log.d(TAG, "keyboard state= $isKeyboardOpen")
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(exchangeRateItem) {
        textState = textState.copy(text = exchangeRateItem.rate)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                focusRequester.requestFocus()
            }
        ,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = exchangeRateItem.symbol,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Left,
            fontSize = if (index == 0) 18.sp else 16.sp,
            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BasicTextField(
                value = textState,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        Log.d(TAG, "is focused= ${it.isFocused}")
                        if (it.isFocused) {
                            textState = textState.copy(
                                selection = TextRange(textState.text.length)
                            )
                        }
                    },
                textStyle = TextStyle().copy(
                    color = LocalContentColor.current,
                    fontSize = if (index == 0) 18.sp else 16.sp,
                    fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Right
                ),
                cursorBrush = SolidColor(LocalContentColor.current),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                onValueChange = { newValue ->
                    if (!newValue.text.matches(Regex("^[0-9.]*\$"))) {
                        return@BasicTextField
                    }
                    val dotCount = newValue.text.count { it == '.' }
                    if (dotCount <= 1) {
                        val decimalLength =
                            if (newValue.text.contains('.'))
                                newValue.text.substringAfter('.').length
                            else
                                0
                        if (decimalLength <= 2) {
                            textState = newValue.copy(
                                selection = TextRange(newValue.text.length)
                            )
                            onValueChange(newValue.text, exchangeRateItem.symbol)
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = exchangeRateItem.fullName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
        }

    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ExchangeRateItemScreenContentPreview() {
    val exchangeRate: Pair<String, Double> = Pair("USD", 1.0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = exchangeRate.first,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Left
        )
        Text(
            text = exchangeRate.second.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            textAlign = TextAlign.Right
        )
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val view = LocalView.current
    var isImeVisible by remember { mutableStateOf(false) }

    DisposableEffect(LocalWindowInfo.current) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            isImeVisible = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true
            true
        }
        view.viewTreeObserver.addOnPreDrawListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnPreDrawListener(listener)
        }
    }
    return rememberUpdatedState(isImeVisible)
}