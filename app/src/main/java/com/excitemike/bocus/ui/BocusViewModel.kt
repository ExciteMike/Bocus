package com.excitemike.bocus.ui

import android.app.Activity
import android.app.Application
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AlarmDatabase
import com.excitemike.bocus.data.Command
import com.excitemike.bocus.data.OfflineBocusRepository
import com.excitemike.bocus.data.cancelSystemAlarm
import com.excitemike.bocus.data.checkSystemPermission
import com.excitemike.bocus.data.getNextAlarmTime
import com.excitemike.bocus.data.scheduleSystemAlarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BocusViewModel(application: Application) : AndroidViewModel(application) {
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

    /** add a new alarm */
    fun addAlarm(name: String) {
        val application: Application = getApplication()

        val size = alarmState.value.size
        if (size < MAX_ALARMS) {
            val tmpAlarm = Alarm(name = name)
            val alarm = tmpAlarm.copy(scheduledAt = getNextAlarmTime(tmpAlarm))
            viewModelScope.launch {
                val id = alarmRepo.insertAlarm(alarm)
                val alarm = alarm.copy(id = id)
                scheduleSystemAlarm(application, alarm)
            }
        } else {
            setErrorMessage(application.getString(R.string.alarm_limit))
        }
    }

    /**
     * true when the given permission has been granted
     */
    fun checkPermission(activity: Activity, permission: String): Boolean {
        return checkSystemPermission(activity, permission)
    }

    /** close the alarm details */
    fun closeAlarmDetails() {
        _uiState.update { it.copy(selectedAlarmIndex = null) }
    }

    /** close and clean up after the confirmation dialog */
    private fun closeConfirmDlg() {
        _uiState.update {
            it.copy(
                confirmMessage = null,
                onConfirm = Command.None,
                onConfirmCancel = Command.None
            )
        }
    }

    /** delete an alarm by id */
    fun deleteAlarmById(id: Int) {
        cancelSystemAlarm(getApplication(), id)
        viewModelScope.launch {
            alarmRepo.deleteAlarm(id)
        }
    }

    /** get rid of the confirmation dlg without doing the thing */
    fun dismissConfirmDlg() {
        val command = _uiState.value.onConfirmCancel
        closeConfirmDlg()
        doCommand(command)
    }

    /** get rid of the error dlg without doing the thing */
    fun dismissErrorDlg() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * execute the provided instruction
     */
    fun doCommand(command: Command) {
        when (command) {
            is Command.None -> Unit
            is Command.DeleteAlarm -> deleteAlarmById(command.alarmId)
            is Command.Callback ->
                viewModelScope.launch {
                    command.cb()
                }
        }
    }

    /**
     * figure out what permissions the app needs
     */
    fun getSystemPermissionsNeeded(): List<Pair<String, Int>> {
        return com.excitemike.bocus.data.getSystemPermissionsNeeded()
    }

    /** the user confirmed. do the thing */
    fun onConfirm() {
        val command = _uiState.value.onConfirm
        closeConfirmDlg()
        doCommand(command)
    }

    /** bring up the alarm details for editing  */
    fun openAlarmDetails(selectedAlarmIndex: Int) {
        _uiState.update { it.copy(selectedAlarmIndex = selectedAlarmIndex) }
    }

    /** bring up a confirmation dialog for deleting an alarm */
    fun requestDeleteAlarm(
        confirmMessage: String,
        onConfirm: Command,
        onCancel: Command
    ) {
        _uiState.update {
            it.copy(
                confirmMessage = confirmMessage,
                onConfirm = onConfirm,
                onConfirmCancel = onCancel
            )
        }
    }

    /** start showing the error message popup */
    fun setErrorMessage(errorMessage: String) {
        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    /**
     * true when the system says we need to show the rationale
     */
    fun shouldShowPermissionRequestRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /** update the values in an alarm and reschedule */
    fun updateAlarmAndReschedule(alarm: Alarm) {
        val application: Application = getApplication()
        viewModelScope.launch {
            alarmRepo.updateAlarm(alarm)
            val newAlarm = alarm.copy(scheduledAt = getNextAlarmTime(alarm))
            scheduleSystemAlarm(application, newAlarm)
        }
    }

    /** update the values in an alarm */
    fun updateAlarmNoReschedule(alarm: Alarm) {
        viewModelScope.launch {
            alarmRepo.updateAlarm(alarm)
        }
    }

    companion object {
        const val MAX_ALARMS = 255
        const val TIMEOUT_MILLIS = 5_000L
    }
}

// TODO: split this up
data class BocusUiState(
    /// Error Message
    var errorMessage: String? = null,
    /// Confirmation Popup Message
    var confirmMessage: String? = null,
    /// Confirmation Popup Action
    var onConfirm: Command = Command.None,
    /// Confirmation Popup Cancel Action
    var onConfirmCancel: Command = Command.None,
    /// index of the selected alarm
    var selectedAlarmIndex: Int? = null,
)