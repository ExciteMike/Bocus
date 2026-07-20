package com.excitemike.bocus.data

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
const val DEFAULT_ACTIVE_DAYS:UByte = 0x3Eu

/// Bit flag for use with Alarm.ActiveDays
const val SUNDAY:UByte = 0x1u
const val MONDAY:UByte = 0x2u
const val TUESDAY:UByte = 0x4u
const val WEDNESDAY:UByte = 0x8u
const val THURSDAY:UByte = 0x10u
const val FRIDAY:UByte = 0x20u
const val SATURDAY:UByte = 0x40u

/// Entry for alarms
data class Alarm(
    /// LazyColumns wants them to have a unique id
    var id: Long,
    /// how to label the alarm in the ui
    var name: String,

    /// every X to Y minutes
    var frequency: Pair<UByte, UByte> = DEFAULT_FREQUENCY,

    /// At what time of day the alarms begin. Minutes since midnight.
    val startTime: UInt = DEFAULT_START,
    /// at what time of day the alarms end. Minutes since midnight.
    val endTime: UInt = DEFAULT_END,

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

fun timeString(minutesSinceMidnight:UInt): String {
    val hour = minutesSinceMidnight / MINUTES_PER_HOUR
    val displayHour: String = when (hour) {
        0u, 12u -> "12"
        in 1u..11u -> "$hour"
        else -> "${hour - 12u}"
    }
    val amPm: String = when (hour) {
        in 0u..12u -> "am"
        else -> "pm"
    }
    val minute = minutesSinceMidnight % MINUTES_PER_HOUR
    val displayMinute = minute.toString().padStart(2, '0')

    return "$displayHour:$displayMinute $amPm"
}