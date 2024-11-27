package com.hayton.airportinfo.currency.data

data class Currency(
    val symbol: String,
    val name: String,
    val symbol_native: String,
    val decimal_digits: Int,
    val rounding: Int,
    val code: String,
    val name_plural: String
)
