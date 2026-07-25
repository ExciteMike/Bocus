package com.excitemike.bocus.ui

import android.Manifest
import android.app.AlarmManager
import android.app.Application
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.data.updateAllSystemAlarms
import com.excitemike.bocus.data.updateSystemAlarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BocusViewModel(application: Application): AndroidViewModel(application) {
    private val dao = AlarmDatabase.getDatabase(application).alarmDao()
    private val alarmRepo = OfflineBocusRepository(dao)
    val alarmState: StateFlow<List<Alarm>> = alarmRepo.getAllAlarmsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )
    private val _uiState = MutableStateFlow(BocusUiState())
    val uiState: StateFlow<BocusUiState> = _uiState.asStateFlow()

    fun addAlarm(name:String, context: Context) {
        val size = alarmState.value.size
        if (size < MAX_ALARMS) {
            viewModelScope.launch {
                alarmRepo.insertAlarm(Alarm(name=name))
            }
        } else {
            setErrorMessage(context.getString(R.string.alarm_limit))
        }
    }

    fun updateAllAlarms() {
        viewModelScope.launch {
            updateAllSystemAlarms(getApplication() as Context, alarmState.value)
        }
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun updateAlarm(alarm:Alarm) {
        val application: Application = getApplication()
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            return
        }
        viewModelScope.launch {
            alarmRepo.updateAlarm(alarm)
            updateSystemAlarm(application, alarm)
        }
    }

    fun goToScreen(screen:AppScreens) {
        _uiState.update { it.copy(currentScreen = screen, selectedAlarmIndex = null) }
    }

    fun setErrorMessage(errorMessage: String) {
        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    fun dismissErrorDlg() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun openAlarmDetails(selectedAlarmIndex: Int) {
        _uiState.update { it.copy(selectedAlarmIndex = selectedAlarmIndex) }
    }

    fun closeAlarmDetails() {
        _uiState.update { it.copy(selectedAlarmIndex = null) }
    }

    fun requestDeleteAlarm(confirmMessage: String, onConfirm: Command) {
        _uiState.update { it.copy(confirmMessage = confirmMessage, onConfirm = onConfirm) }
    }
    companion object {
        const val MAX_ALARMS = 255
        const val TIMEOUT_MILLIS = 5_000L
    }
}

data class BocusUiState (
    var currentScreen:AppScreens = AppScreens.WELCOME,
    /// Error Message
    var errorMessage: String? = null,
    /// Confirmation Popup Message
    var confirmMessage: String? = null,
    /// Confirmation Popup Action
    var onConfirm: Command = Command.None,
    /// index of the selected alarm
    var selectedAlarmIndex: Int? = null,
)