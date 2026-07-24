/**
 * functions for dealing with Android's alarm system
 */
package com.excitemike.bocus.data

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.excitemike.bocus.receiver.AlarmReceiver
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

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
 * find the closest future occurrence of the alarm
 */
fun getNextAlarmTime(alarm:Alarm): Long {
    val now = LocalDateTime.now()
    val nowDate = now.toLocalDate()
    val nowMillis = System.currentTimeMillis()
    val dayOfWeek = now.dayOfWeek
    val minutesBetween = Random.nextLong(alarm.frequencyMin.toLong(), alarm.frequencyMax.toLong())
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
 * make sure that what we have registered with the system is in sync with what we have
 * in our data
 */
fun updateAllSystemAlarms(context: Context, alarms: List<Alarm>) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    TODO()
}

/**
 * If the alarm is already scheduled, cancel it. Then schedule one based on the given Alarm
 */
@RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
fun updateSystemAlarm(context: Context, alarm: Alarm) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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

fun cancelSystemAlarm(alarmManager: AlarmManager) {
    alarmManager.cancel {  }
}