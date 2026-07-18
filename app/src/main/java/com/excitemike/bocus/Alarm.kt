package com.excitemike.bocus

/// what it does when the alarm goes off
enum class NotifMode {
    Bell,
    Vibrate,
    BellAndVibrate
}

/// which days to do the alarm on
data class ActiveDays(
    var su: Boolean,
    var mo: Boolean,
    var tu: Boolean,
    var ee: Boolean,
    var th: Boolean,
    var fr: Boolean,
    var sa: Boolean,
)

/// Entry for alarms
data class Alarm(
    /// at what time of day the alarms begin
    val startTime: Int,
    /// at what time of day the alarms end
    val endTime: Int,
    /// every X to Y minutes
    var frequency: Pair<Int, Int>,
    /// what to do
    var notifMode: NotifMode,
    /// message to put on the phone notification
    var message: String,
    /// whether the alarm repeats if not dismissed
    var requireDismiss: Boolean,
    /// how long it sounds/buzzes for
    var alarmLength: Pair<Int, Int>,
    /// repeat on which days of the week
    var weekDays: ActiveDays,
)
