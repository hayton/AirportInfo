package com.hayton.airportinfo.currency.viewmodel

import android.util.Log
import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hayton.airportinfo.currency.data.Currency
import com.hayton.airportinfo.currency.data.ExchangeRateItemObject
import com.hayton.airportinfo.currency.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class CurrencyScreenViewModel @Inject constructor(
    private val repository: CurrencyRepository
) : ViewModel() {
    private val TAG = "CurrencyScreenViewModel"

    private val _currenciesStateFlow = MutableStateFlow<List<Pair<String, Currency>>>(emptyList())
    val currenciesStateFlow = _currenciesStateFlow.asStateFlow()

    private val _exchangeRateStateFlow = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val exchangeRateStateFlow = _exchangeRateStateFlow.asStateFlow()

    private val _exchangeRateItemObjectList =
        MutableStateFlow<List<ExchangeRateItemObject>>(emptyList())
    val exchangeRateItemObjectList = _exchangeRateItemObjectList.asStateFlow()

    private val _isLoadingStateFlow = MutableStateFlow(true)
    val isLoadingStateFlow = _isLoadingStateFlow.asStateFlow()

    private var retryCount = 0

    fun clearFocusedItem(focusedIndex: Int) {
        _exchangeRateItemObjectList.value =
            _exchangeRateItemObjectList.value.mapIndexed { index, exchangeRateItemObject ->
                if (index == focusedIndex) {
                    exchangeRateItemObject.copy(rate = "")
                } else {
                    exchangeRateItemObject
                }
            }
    }

    fun updateFocusedItem(focusedIndex: Int, char: String) {
        _exchangeRateItemObjectList.value =
            _exchangeRateItemObjectList.value.mapIndexed { index, exchangeRateItemObject ->
                if (index == focusedIndex) {
                    if (char == "err") {
                        exchangeRateItemObject.copy(rate = "err")
                    } else {
                        val originalValue = exchangeRateItemObject.rate
                        if (isNextCharacterAllowed(originalValue, char.toCharArray()[0])) {
                            exchangeRateItemObject.copy(rate = originalValue + char)
                        } else {
                            exchangeRateItemObject
                        }
                    }
                } else {
                    exchangeRateItemObject
                }
            }
    }

    fun removeLastChar(focusedIndex: Int) {
        _exchangeRateItemObjectList.value =
            _exchangeRateItemObjectList.value.mapIndexed { index, exchangeRateItemObject ->
                if (index == focusedIndex) {
                    val originalValue = exchangeRateItemObject.rate
                    val newValue = if (originalValue.length == 1) {
                        ""
                    } else {
                        originalValue.dropLast(1)
                    }
                    exchangeRateItemObject.copy(rate = newValue)
                } else {
                    exchangeRateItemObject
                }
            }
    }

    fun calculateResult(focusedIndex: Int) {
        val regex = """^(\s*[-+]?\s*\d+(\.\d{1,2})?\s*)([÷×\-\+]\s*[-+]?\s*\d+(\.\d{1,2})?\s*)*$""".toRegex()
        val baseItem = _exchangeRateItemObjectList.value[focusedIndex]
        val baseCurrency = baseItem.symbol
        val mathExpression = baseItem.rate

//        Log.d(TAG, "is valid= ${mathExpression.matches(regex)}")
        if (mathExpression.matches(regex)) {
            val result = evaluateExpression(mathExpression)
            if (result is Double) {
                calculateExchangeAmount(result, baseCurrency)
            } else if (result is String) {
                updateFocusedItem(focusedIndex, result)
            }
        }
    }

    private fun calculateExchangeAmount(baseValue: Double, baseCurrency: String) {
        val baseRate = _exchangeRateStateFlow.value.find { it.first == baseCurrency }?.second

        baseRate?.let {
            _exchangeRateItemObjectList.value =
                _exchangeRateItemObjectList.value.map { itemObject ->
                    val equivalentRate =
                        (_exchangeRateStateFlow.value.find { it.first == itemObject.symbol }?.second
                            ?: 0.0) / baseRate

                    val amount = BigDecimal(baseValue * equivalentRate)
                        .setScale(2, RoundingMode.HALF_EVEN)
                    itemObject.copy(rate = amount.toString())
                }
        }
    }

    fun getCurrencies(
        currencies: List<String> = listOf("USD", "EUR", "SGD", "JPY", "GBP", "CHF", "HKD")
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getCurrencies(currencies)
            if (response.isSuccessful) {
                val data = response.body()?.data?.toList() ?: emptyList()
                _currenciesStateFlow.value = data
                getExchangeRate("USD")
            } else {
                retryCount++
                if (retryCount < 3) {
                    getCurrencies()
                }
            }
        }
    }

    private fun getExchangeRate(
        baseCurrency: String,
        currencies: List<String> = listOf("USD", "EUR", "SGD")
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getExchangeRate(baseCurrency, currencies)
            _isLoadingStateFlow.value = false
            if (response.isSuccessful) {
                val data = response.body()?.data?.toList() ?: emptyList()
                _exchangeRateStateFlow.value = data
                _exchangeRateItemObjectList.value = data.map { exchangeRate ->
                    ExchangeRateItemObject(
                        symbol = exchangeRate.first,
                        rate = BigDecimal(exchangeRate.second).setScale(2, RoundingMode.HALF_EVEN)
                            .toPlainString(),
                        fullName = _currenciesStateFlow.value.find { it.first == exchangeRate.first }?.second?.name
                            ?: ""
                    )
                }.toMutableList().let {
                    val usdExchangeRateItemObject = it.find { item -> item.symbol == "USD" }

                    usdExchangeRateItemObject?.apply {
                        it.removeIf { item -> item.symbol == "USD" }
                        it.add(0, this)
                    }
                    it
                }
            } else {
                _exchangeRateStateFlow.value = emptyList()
            }
        }
    }

    private fun isNextCharacterAllowed(currentInput: String, nextChar: Char): Boolean {
        val operators = setOf('÷', '×', '-', '+')
        val newInput = currentInput + nextChar

        // 1. Prevent starting with an operator
        if (currentInput.isEmpty() && nextChar in operators) {
            return false
        }

        // 2. Check for two consecutive decimal dots
        if (currentInput.endsWith('.') && nextChar == '.') {
            return false
        }

        // 3. Check for two consecutive operation symbols
        if (currentInput.isNotEmpty() && currentInput.last() in operators && nextChar in operators) {
            return false
        }

        // 4. Split the input into tokens using operators as delimiters
        val tokens = newInput.split(Regex("[÷×\\-\\+]"))

        // 5. Get the last token, which is the current number being entered
        val currentNumber = tokens.lastOrNull() ?: ""

        // 6. Check for multiple decimal points in the current number
        if (currentNumber.count { it == '.' } > 1) {
            return false
        }

        // 7. Check if the decimal part exceeds two digits
        if (currentNumber.contains('.')) {
            val decimalPart = currentNumber.substringAfter('.', "")
            if (decimalPart.length > 2) {
                return false
            }
        }

        // 8. If none of the rules are violated, the next character is allowed
        return true
    }

    private fun evaluateExpression(expression: String): Any {
        // Step 1: Preprocess the expression
        val preprocessedExpression = preprocessExpression(expression)

        // Step 2: Tokenize the expression
        val tokens = tokenize(preprocessedExpression)

        // Step 3: Convert to Reverse Polish Notation (RPN)
        val rpn = toRPN(tokens)

        // Step 4: Evaluate the RPN expression
        return evaluateRPN(rpn)
    }

// Helper functions

    private fun preprocessExpression(expression: String): String {
        return expression
            .replace('×', '*')
            .replace('÷', '/')
    }

    private fun tokenize(expression: String): List<String> {
        val tokens = mutableListOf<String>()
        var numberBuffer = StringBuilder()

        var i = 0
        while (i < expression.length) {
            val ch = expression[i]

            if (ch.isDigit() || ch == '.') {
                numberBuffer.append(ch)
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                if (numberBuffer.isNotEmpty()) {
                    tokens.add(numberBuffer.toString())
                    numberBuffer = StringBuilder()
                }

                // Handle negative numbers
                if (ch == '-' && (i == 0 || expression[i - 1] in "+-*/")) {
                    numberBuffer.append(ch)
                } else {
                    tokens.add(ch.toString())
                }
            } else {
                // Invalid character
                throw IllegalArgumentException("Invalid character: $ch")
            }
            i++
        }

        if (numberBuffer.isNotEmpty()) {
            tokens.add(numberBuffer.toString())
        }

        return tokens
    }

    private fun toRPN(tokens: List<String>): List<String> {
        val outputQueue = mutableListOf<String>()
        val operatorStack = mutableListOf<String>()

        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2
        )

        for (token in tokens) {
            when {
                token.isNumber() -> {
                    outputQueue.add(token)
                }
                token.isOperator() -> {
                    while (operatorStack.isNotEmpty() &&
                        precedence.getValue(operatorStack.last()) >= precedence.getValue(token)) {
                        outputQueue.add(operatorStack.removeAt(operatorStack.size - 1))
                    }
                    operatorStack.add(token)
                }
                else -> {
                    throw IllegalArgumentException("Invalid token: $token")
                }
            }
        }

        while (operatorStack.isNotEmpty()) {
            outputQueue.add(operatorStack.removeAt(operatorStack.size - 1))
        }

        return outputQueue
    }

    private fun evaluateRPN(tokens: List<String>): Any {
        val stack = mutableListOf<Double>()

        for (token in tokens) {
            when {
                token.isNumber() -> {
                    stack.add(token.toDouble())
                }
                token.isOperator() -> {
                    if (stack.size < 2) {
                        throw IllegalArgumentException("Invalid expression")
                    }
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (b == 0.0) {
                                return "err"
                            } else {
                                a / b
                            }
                        }
                        else -> throw IllegalArgumentException("Unknown operator: $token")
                    }
                    stack.add(result)
                }
                else -> {
                    throw IllegalArgumentException("Invalid token: $token")
                }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression")
        }

        return stack[0]
    }

// Extensions

    private fun String.isNumber(): Boolean {
        return this.toDoubleOrNull() != null
    }

    private fun String.isOperator(): Boolean {
        return this in setOf("+", "-", "*", "/")
    }

}