/**
 * functions for dealing with Android's alarm system
 */
package com.excitemike.bocus.data

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.excitemike.bocus.R
import com.excitemike.bocus.receiver.AlarmReceiver
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

private val PERMISSIONS = listOf(
    android.Manifest.permission.USE_EXACT_ALARM to R.string.explain_alarm_permission,
    android.Manifest.permission.VIBRATE to R.string.explain_vibrate_permission,
    android.Manifest.permission.POST_NOTIFICATIONS to R.string.explain_notification_permission)

/**
 * Check that the java.time.DayOfWeek time is selected for the alarm, or no day of week is so
 * it defaults to all of them
 */
fun checkDayOfWeek(javaDoW: DayOfWeek, alarm: Alarm): Boolean {
   if (alarm.activeDays == 0) {
       return true
   }
    return when (javaDoW) {
        DayOfWeek.SUNDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.SUNDAY)!=0
        DayOfWeek.MONDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.MONDAY)!=0
        DayOfWeek.TUESDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.TUESDAY)!=0
        DayOfWeek.WEDNESDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.WEDNESDAY)!=0
        DayOfWeek.THURSDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.THURSDAY)!=0
        DayOfWeek.FRIDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.FRIDAY)!=0
        DayOfWeek.SATURDAY -> (alarm.activeDays and AlarmDayOfWeekFlags.SUNDAY)!=0
    }
}

/**
 * Check that the given hour (0-23) and minute (0-59) is within the window specified by the alarm
 */
fun checkTimeOfDay(hourOfDay: Int, minuteOfHour: Int, alarm: Alarm): Boolean {
    val minuteOfDay = hourOfDay * 60 + minuteOfHour
    val alarmStartMinuteOfDay = alarm.startHour * 60 + alarm.startMinute
    val alarmEndMinuteOfDay = alarm.endHour * 60 + alarm.endMinute
    return minuteOfDay in alarmStartMinuteOfDay..alarmEndMinuteOfDay
}

/**
 * true when the given permission has been granted
 */
fun checkSystemPermission(activity: Activity, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(activity.application, permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
}

/**
 * find the closest future occurrence of the alarm
 */
fun getNextAlarmTime(alarm:Alarm): Long {
    val now = LocalDateTime.now()
    val nowDate = now.toLocalDate()
    val nowMillis = System.currentTimeMillis()
    val dayOfWeek = now.dayOfWeek
    val minutesBetween = if (alarm.frequencyMax <= alarm.frequencyMin) {
        alarm.frequencyMin.toLong()
    } else {
        Random.nextLong(alarm.frequencyMin.toLong(), alarm.frequencyMax.toLong())
    }
    val millisBetween = minutesBetween * 60 * 1000
    val todayStartTimeMillis = LocalDateTime.of(nowDate, LocalTime.of(alarm.startHour, alarm.startMinute)).toEpochSecond(ZoneOffset.UTC) * 1000
    val todayEndTimeMillis =  LocalDateTime.of(nowDate, LocalTime.of(alarm.endHour, alarm.endMinute)).toEpochSecond(ZoneOffset.UTC) * 1000
    val nextTimeTodayMillis = max(max(nowMillis, alarm.lastTriggeredAt.toLong() + millisBetween), todayStartTimeMillis)

    // do it today, if in window
    val validToday = checkDayOfWeek(dayOfWeek, alarm) and (nextTimeTodayMillis in (todayStartTimeMillis..todayEndTimeMillis))
    if (validToday) {
        return nextTimeTodayMillis
    }

    // find how many days ahead to schedule it
    var daysAhead:Long = 0
    for (i in 0..6) {
        val dayToCheck = dayOfWeek.plus(i.toLong())
        if (checkDayOfWeek(dayToCheck, alarm)) {
            daysAhead = i.toLong()
            break
        }
    }

    val futureDate = nowDate.plus(daysAhead, ChronoUnit.DAYS)
    val futureStartTime = LocalTime.of(alarm.startHour, alarm.startMinute)
    val futureStartMillis = LocalDateTime.of(futureDate, futureStartTime).toEpochSecond(ZoneOffset.UTC) * 1000
    val alarmTime = min(alarm.lastTriggeredAt.toLong() + millisBetween, futureStartMillis)
    return alarmTime
}

/**
 * get a list of permissions we still need to request
 */
fun getSystemPermissionExplanationsNeeded(activity: Activity): List<Pair<String, Int>> {
    val requestsNeeded = mutableListOf<Pair<String, Int>>()
    for (pair in PERMISSIONS) {
        if (ContextCompat.checkSelfPermission(activity.application, pair.first) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, pair.first)) {
                Log.v("Bocus", "explanation needed: ${pair.first}")
                requestsNeeded.add(pair)
            }
        }
    }
    return requestsNeeded
}

/**
 * get a list of permissions this app needs
 */
fun getSystemPermissionsNeeded(activity: Activity): List<Pair<String, Int>> = PERMISSIONS


private const val PERMISSIONS_REQUEST_CODE = 123

/**
 * make sure that what we have registered with the system is in sync with what we have
 * in our data
 */
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
fun updateSystemAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (!alarmManager.canScheduleExactAlarms()) {
        Log.v("Bocus", "can't schedule")
        return
    }

    val triggerAtMillis = getNextAlarmTime(alarm)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarm.id!!,
        Intent (context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_NAME_TITLE, alarm.name)
            putExtra(AlarmReceiver.EXTRA_NAME_MESSAGE, alarm.message)
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
private const val PENDING_INTENT_FLAGS = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
