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
import com.excitemike.bocus.data.cancelSystemAlarm
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

    /** add a new alarm */
    fun addAlarm(name:String, context: Context) {
        val size = alarmState.value.size
        if (size < MAX_ALARMS) {
            viewModelScope.launch {
                alarmRepo.insertAlarm(Alarm(name=name))
            }
            TODO("schedule alarm")
        } else {
            setErrorMessage(context.getString(R.string.alarm_limit))
        }
    }

    /** close the alarm details */
    fun closeAlarmDetails() {
        _uiState.update { it.copy(selectedAlarmIndex = null) }
    }

    /** close and clean up after the confirmation dialog */
    fun closeConfirmDlg() {
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
        viewModelScope.launch {
            alarmRepo.deleteAlarm(id)
        }
        cancelSystemAlarm(getApplication(), id)
    }

    /** get rid of the confirmation dlg without doing the thing */
    fun dismissConfirmDlg() {
        doCommand(_uiState.value.onConfirmCancel)
        closeConfirmDlg()
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

    /** transition from one UI screen to another */
    fun goToScreen(screen:AppScreens) {
        _uiState.update { it.copy(currentScreen = screen, selectedAlarmIndex = null) }
    }

    /** the user confirmed. do the thing */
    fun onConfirm() {
        doCommand(_uiState.value.onConfirm)
        closeConfirmDlg()
    }

    /** bring up the alarm details for editing  */
    fun openAlarmDetails(selectedAlarmIndex: Int) {
        _uiState.update { it.copy(selectedAlarmIndex = selectedAlarmIndex) }
    }

    /** bring up a confirmation dialog for deleting an alarm */
    fun requestDeleteAlarm(confirmMessage: String, onConfirm: Command, onCancel: Command) {
        _uiState.update { it.copy(confirmMessage = confirmMessage, onConfirm = onConfirm, onConfirmCancel = onCancel) }
    }

    /** start showing the error message popup */
    fun setErrorMessage(errorMessage: String) {
        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    /** update the values in an alarm */
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

    /** update the system alarms to match the DB */
    fun updateAllSystemAlarms() {
        viewModelScope.launch {
            updateAllSystemAlarms(getApplication() as Context, alarmState.value)
        }
    }

    companion object {
        const val MAX_ALARMS = 255
        const val TIMEOUT_MILLIS = 5_000L
    }
}

// TODO: split this up
data class BocusUiState (
    var currentScreen:AppScreens = AppScreens.ALARMS,
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