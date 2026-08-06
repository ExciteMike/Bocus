package com.excitemike.bocus.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDao
import com.excitemike.bocus.data.AlarmId
import com.excitemike.bocus.data.cancelSystemAlarm
import com.excitemike.bocus.data.chooseMessage
import com.excitemike.bocus.data.getNextAlarmTime
import com.excitemike.bocus.data.scheduleSystemAlarm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val MAX_ALARMS = 255
const val TIMEOUT_MILLIS = 5_000L

class AlarmScreenViewModel(
    private val alarmDao: AlarmDao
) : ViewModel() {

    /**
     * All alarms in a StateFlow
     */
    val allAlarmsState: StateFlow<List<Alarm>> = alarmDao.getAllAlarms()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    /** add a new alarm */
    fun addAlarm(
        context: Context,
        name: String,
        onError: (Int) -> Unit
    ) {
        val size = allAlarmsState.value.size
        if (size < MAX_ALARMS) {
            val tmpAlarm = Alarm(name = name)
            val alarm = tmpAlarm.copy(scheduledAt = getNextAlarmTime(tmpAlarm))
            viewModelScope.launch {
                val id = alarmDao.insert(alarm)
                val alarm = alarm.copy(id = id)
                val message = chooseMessage(context, alarm)
                scheduleSystemAlarm(context, alarm, message)
            }
        } else {
            onError(R.string.alarm_limit)
        }
    }

    /** delete an alarm by id */
    fun deleteAlarmById(
        context: Context,
        alarmId: Long
    ) {
        cancelSystemAlarm(context, alarmId)
        viewModelScope.launch {
            alarmDao.delete(AlarmId(alarmId))
        }
    }

    /** update the values in an alarm and reschedule */
    fun updateAlarmAndReschedule(
        context: Context,
        alarm: Alarm
    ) {
        viewModelScope.launch {
            alarmDao.update(alarm)
            val newAlarm = alarm.copy(scheduledAt = getNextAlarmTime(alarm))
            val message = chooseMessage(context, alarm)
            scheduleSystemAlarm(context, newAlarm, message)
        }
    }

    /** update the values in an alarm */
    fun updateAlarmNoReschedule(alarm: Alarm) {
        viewModelScope.launch {
            alarmDao.update(alarm)
        }
    }

    /**
     * Which alarm it is for if we are showing AlarmDetails
     */
    private val selectedAlarmId = MutableStateFlow<Long?>(null)

    /**
     * Stateflow for selected alarm
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedAlarmState: StateFlow<Alarm?> =
        selectedAlarmId.flatMapLatest { selectedAlarmId ->
            if (selectedAlarmId == null) {
                flowOf(null)
            } else {
                alarmDao.getAlarm(selectedAlarmId)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            null
        )

    /**
     * Clear selected alarm
     */
    fun clearSelectedAlarm() {
        selectedAlarmId.value = null
    }

    /**
     * Select an alarm and show the alarm details screen
     */
    fun loadSelectedAlarm(alarmId: Long) {
        selectedAlarmId.value = alarmId
    }
}