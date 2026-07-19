package com.excitemike.bocus.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.util.Calendar

/// what it does when the alarm goes off
enum class NotifMode(val value:UByte) {
    Bell(0x01u),
    Vibrate(0x10u),
    BellAndVibrate(0x11u)
}

const val MINUTES_PER_HOUR:UInt = 60u
val TEN_AM:UInt = 10u * MINUTES_PER_HOUR
val FIVE_PM:UInt = 17u * MINUTES_PER_HOUR
val DEFAULT_START = TEN_AM
val DEFAULT_END = FIVE_PM
const val DEFAULT_FREQUENCY_MIN:UByte = 15u
const val DEFAULT_FREQUENCY_MAX:UByte = 25u
val DEFAULT_FREQUENCY = Pair(DEFAULT_FREQUENCY_MIN, DEFAULT_FREQUENCY_MAX)
val DEFAULT_NOTIF_MODE = NotifMode.BellAndVibrate
const val DEFAULT_ALARM_LENGTH:UByte = 30u
const val DEFAULT_ACTIVE_DAYS:UByte = 0x7u

/// Bit flag for use with Alarm.ActiveDays
const val SUNDAY:UByte = 0x1u
const val MONDAY:UByte = 0x1u
const val TUESDAY:UByte = 0x1u
const val WEDNESDAY:UByte = 0x1u
const val THURSDAY:UByte = 0x1u
const val FRIDAY:UByte = 0x1u
const val SATURDAY:UByte = 0x1u

/// Entry for alarms
data class Alarm(
    /// how to label the alarm in the ui
    var name: String,
    /// At what time of day the alarms begin. Minutes since midnight.
    val startTime: UInt = DEFAULT_START,
    /// at what time of day the alarms end. Minutes since midnight.
    val endTime: UInt = DEFAULT_END,
    /// every X to Y minutes
    var frequency: Pair<UByte, UByte> = DEFAULT_FREQUENCY,
    /// what to do
    var notifMode: NotifMode = DEFAULT_NOTIF_MODE,
    /// message to put on the phone notification
    var message: String = "",
    /// whether the alarm repeats if not dismissed
    var requireDismiss: Boolean = false,
    /// how long it sounds/buzzes for. Seconds
    var alarmLength: UByte = DEFAULT_ALARM_LENGTH,
    /// repeat on which days of the week, (bitflags)
    var activeDays: UByte = DEFAULT_ACTIVE_DAYS,
)
