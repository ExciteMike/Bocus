/**
 * functions for dealing with Android's alarm system
 */
package com.excitemike.bocus.data

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.excitemike.bocus.R
import com.excitemike.bocus.receiver.AlarmReceiver
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

private val PERMISSIONS = listOf(
    Manifest.permission.POST_NOTIFICATIONS to R.string.explain_notification_permission
)

/**
 * Check that the java.time.DayOfWeek time is selected for the alarm, or no day of week is so
 * it defaults to all of them
 */
fun checkDayOfWeek(javaDoW: DayOfWeek, alarm: Alarm): Boolean {
    // TODO: remove debug early return
    return true
    if (alarm.activeDays == 0) {
        return true
    }
    return when (javaDoW) {
        DayOfWeek.SUNDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.SUNDAY) != 0
        DayOfWeek.MONDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.MONDAY) != 0
        DayOfWeek.TUESDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.TUESDAY) != 0
        DayOfWeek.WEDNESDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.WEDNESDAY) != 0
        DayOfWeek.THURSDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.THURSDAY) != 0
        DayOfWeek.FRIDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.FRIDAY) != 0
        DayOfWeek.SATURDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.SUNDAY) != 0
    }
}

/**
 * true when the given permission has been granted
 */
fun checkSystemPermission(activity: Activity, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        activity.application,
        permission
    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

/**
 * conversion
 */
fun millisToZonedDateTime(millis: Long): ZonedDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(millis),
        ZoneId.systemDefault()
    ).atZone(ZoneId.systemDefault())
}

/**
 * conversion
 */
fun localDateAndTimeToMillis(d: LocalDate, t: LocalTime): Long {
    return LocalDateTime.of(d, t)
        .atZone(ZoneId.systemDefault())
        .toEpochSecond() * 1000
}

/**
 * find the closest future occurrence of the alarm
 */
fun getNextAlarmTime(alarm: Alarm): Long {
    val nowMillis = System.currentTimeMillis()
    val nowDateTime = millisToZonedDateTime(nowMillis)
    val nowDate = nowDateTime.toLocalDate()
    val alarmStartLocalTime = LocalTime.of(alarm.startHour, alarm.startMinute)
    val alarmEndLocalTime = LocalTime.of(alarm.endHour, alarm.endMinute)
    val todayStartTimeMillis = localDateAndTimeToMillis(nowDate, alarmStartLocalTime)
    val todayEndTimeMillis = localDateAndTimeToMillis(nowDate, alarmEndLocalTime)
    val dayOfWeek = nowDate.dayOfWeek
    val minutesBetween = if (alarm.frequencyMax <= alarm.frequencyMin) {
        alarm.frequencyMin.toLong()
    } else {
        Random.nextLong(alarm.frequencyMin.toLong(), alarm.frequencyMax.toLong())
    }
    val millisBetween = minutesBetween * 60 * 1000
    val nextTimeTodayMillis =
        maxOf(nowMillis, alarm.lastTriggeredAt, todayStartTimeMillis) + millisBetween


    // do it today, if in window
    val validToday = checkDayOfWeek(
        dayOfWeek,
        alarm
    ) and (nextTimeTodayMillis in (todayStartTimeMillis..todayEndTimeMillis))
    if (validToday) {
        return nextTimeTodayMillis
    }

    // find how many days ahead to schedule it
    var daysAhead: Long = 0
    for (i in 0..6) {
        val dayToCheck = dayOfWeek.plus(i.toLong())
        if (checkDayOfWeek(dayToCheck, alarm)) {
            daysAhead = i.toLong()
            break
        }
    }

    val futureDate = nowDate.plus(daysAhead, ChronoUnit.DAYS)
    val futureStartTime = LocalTime.of(alarm.startHour, alarm.startMinute)
    val futureStartMillis = localDateAndTimeToMillis(futureDate, futureStartTime)
    val alarmTime = maxOf(alarm.lastTriggeredAt, futureStartMillis) + millisBetween

    return alarmTime
}

/**
 * get a list of permissions this app needs
 */
fun getSystemPermissionsNeeded(): List<Pair<String, Int>> = PERMISSIONS

/**
 * make sure that what we have registered with the system is in sync with what we have
 * in our data
 */
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun updateAllSystemAlarms(context: Context, alarms: List<Alarm>) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        Log.v("Bocus", "can't schedule")
        return
    }
    for (alarm in alarms) {
        updateSystemAlarm(context, alarm)
    }
}

/**
 * If the alarm is already scheduled, cancel it. Then schedule one based on the given Alarm
 */
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun updateSystemAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        Log.v("Bocus", "can't schedule")
        return
    }

    val triggerAtMillis = getNextAlarmTime(alarm)
    val dateTimeString = Instant
        .ofEpochSecond(triggerAtMillis / 1000L)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .toString()

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id!!,
        Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.PLAY_ALARM_ACTION
            putExtra(AlarmReceiver.EXTRA_NAME_TITLE, alarm.name)

            // TODO: uncomment and delete debug thing below putExtra(AlarmReceiver.EXTRA_NAME_MESSAGE, alarm.message)
            putExtra(
                AlarmReceiver.EXTRA_NAME_MESSAGE,
                "scheduled for $dateTimeString\n$triggerAtMillis"
            )

            putExtra(AlarmReceiver.EXTRA_NAME_ALARM_ID, alarm.id)
        },
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
    )
    if (!alarmManager.canScheduleExactAlarms()) {
        return
    }
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )
}

/**
 * cancel an alarm potentially set by this app
 */
fun cancelSystemAlarm(context: Context, alarmId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(
        PendingIntent.getBroadcast(
            context,
            alarmId,
            Intent(context, AlarmReceiver::class.java),
            PENDING_INTENT_FLAGS
        )
    )
}

/**
 * intent flags used by Bocus
 */
private const val PENDING_INTENT_FLAGS =
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
