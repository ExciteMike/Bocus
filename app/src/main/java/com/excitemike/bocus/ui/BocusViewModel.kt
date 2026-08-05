package com.excitemike.bocus.ui

import android.app.Activity
import android.app.Application
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.AlarmId
import com.excitemike.bocus.data.cancelSystemAlarm
import com.excitemike.bocus.data.checkSystemPermission
import com.excitemike.bocus.data.getNextAlarmTime
import com.excitemike.bocus.data.scheduleSystemAlarm
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BocusViewModel(application: Application) : AndroidViewModel(application) {
    val alarmDao = AlarmDatabase.getDatabase(application).alarmDao()
    val alarmState: StateFlow<List<Alarm>> = alarmDao.getAllAlarms()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    /** add a new alarm */
    fun addAlarm(
        name: String,
        onError: (Int) -> Unit
    ) {
        val application: Application = getApplication()

        val size = alarmState.value.size
        if (size < MAX_ALARMS) {
            val tmpAlarm = Alarm(name = name)
            val alarm = tmpAlarm.copy(scheduledAt = getNextAlarmTime(tmpAlarm))
            viewModelScope.launch {
                val id = alarmDao.insert(alarm)
                val alarm = alarm.copy(id = id)
                scheduleSystemAlarm(application, alarm)
            }
        } else {
            onError(R.string.alarm_limit)
        }
    }

    /**
     * true when the given permission has been granted
     */
    fun checkPermission(activity: Activity, permission: String): Boolean {
        return checkSystemPermission(activity, permission)
    }

    /** delete an alarm by id */
    fun deleteAlarmById(alarmId: Long) {
        cancelSystemAlarm(getApplication(), alarmId)
        viewModelScope.launch {
            alarmDao.delete(AlarmId(alarmId))
        }
    }

    /**
     * figure out what permissions the app needs
     */
    fun getSystemPermissionsNeeded(): List<Pair<String, Int>> {
        return com.excitemike.bocus.data.getSystemPermissionsNeeded()
    }

    /**
     * true when the system says we need to show the rationale
     */
    @Suppress("unused")
    fun shouldShowPermissionRequestRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /** update the values in an alarm and reschedule */
    fun updateAlarmAndReschedule(alarm: Alarm) {
        val application: Application = getApplication()
        viewModelScope.launch {
            alarmDao.update(alarm)
            val newAlarm = alarm.copy(scheduledAt = getNextAlarmTime(alarm))
            scheduleSystemAlarm(application, newAlarm)
        }
    }

    /** update the values in an alarm */
    fun updateAlarmNoReschedule(alarm: Alarm) {
        viewModelScope.launch {
            alarmDao.update(alarm)
        }
    }

    companion object {
        const val MAX_ALARMS = 255
        const val MAX_MESSAGE_LISTS = 255
        const val MAX_MESSAGES_PER_LIST = 255
        const val MAX_NAME_LEN = 255
        const val MAX_MESSAGE_LEN = 255
        const val TIMEOUT_MILLIS = 5_000L
    }
}
