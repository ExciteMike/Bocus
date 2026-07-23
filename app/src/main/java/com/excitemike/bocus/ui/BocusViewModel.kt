package com.excitemike.bocus.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.BocusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BocusViewModel(private val alarmRepo: BocusRepository): ViewModel() {
    val alarmState: StateFlow<List<Alarm>> = alarmRepo.getAllAlarmsStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )
    private val _uiState = MutableStateFlow(BocusUiState())
    val uiState: StateFlow<BocusUiState> = _uiState.asStateFlow()

    private val _selectedAlarm = MutableStateFlow<Int?>(null)
    val selectedAlarm = _selectedAlarm.asStateFlow()

    fun addAlarm(name:String, context: Context) {
        val size = alarmState.value.size
        if (size < MAX_ALARMS) {
            viewModelScope.launch {
                alarmRepo.insertAlarm(Alarm(name=name))
            }
            /*
            val alarm = Alarm(id=getNextAlarmId(), name=name)
            _uiState.update { it.copy(alarms = it.alarms + alarm) }*/
        } else {
            setErrorMessage(context.getString(R.string.alarm_limit))
        }
    }

    fun goToScreen(screen:AppScreens) {
        _uiState.update { it.copy(currentScreen = screen, selectedAlarm = null) }
    }

    fun setErrorMessage(errorMessage: String) {
        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    fun dismissErrorDlg() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun openAlarmDetails(selectedAlarm: Int) {
        _uiState.update { it.copy(selectedAlarm = selectedAlarm) }
    }

    fun closeAlarmDetails() {
        _uiState.update { it.copy(selectedAlarm = null) }
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
    /// index of the selected alarm
    var selectedAlarm: Int? = null,
)