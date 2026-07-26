/**
 * Useful things that didn't have an otherwise obvious place to put them
 */
package com.excitemike.bocus.util

/** true if the desiredBits are all 1 in bits */
fun checkFlags(bits:Int, desiredBits:Int): Boolean {
    return desiredBits == (bits and desiredBits)
}

/** convert an hour (0-23) and minute (0-59) to a time in a format like "1:23 pm" */
fun timeString(format:String, hour:Int, minute:Int): String {
    val displayHour: String = when (hour) {
        0, 12 -> "12"
        in 1..11 -> "$hour"
        else -> "${hour - 12}"
    }
    val amPm: String = when (hour) {
        in 0..12 -> "am"
        else -> "pm"
    }
    val displayMinute = minute.toString().padStart(2, '0')

    return String.format(
        format,
        displayHour,
        displayMinute,
        amPm)
}