package com.excitemike.bocus.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDao
import com.excitemike.bocus.data.MessageListDao
import com.excitemike.bocus.ui.BocusViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class AlarmScreenViewModel(
    private val alarmDao: AlarmDao
) : ViewModel() {
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
            SharingStarted.WhileSubscribed(BocusViewModel.TIMEOUT_MILLIS),
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