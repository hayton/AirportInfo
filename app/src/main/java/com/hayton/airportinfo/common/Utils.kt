package com.hayton.airportinfo.common

fun Char.isChineseCharacter(): Boolean {
    val unicodeBlock = Character.UnicodeBlock.of(this)
    return unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
            unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
            unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
            unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B ||
            unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
            unicodeBlock == Character.UnicodeBlock.GENERAL_PUNCTUATION
}

fun String.flightStatusFormat(): String {
    val index = this.indexOfFirst { !it.isChineseCharacter() }
    return if (index > 0) {
        this.substring(0, index) + "\n" + this.substring(index)
    } else {
        this
    }
}
