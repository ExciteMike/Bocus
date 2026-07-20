package com.excitemike.bocus.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

class BocusViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(BocusUiState(alarms = listOf(
        Alarm(id=0, "Test Alarm", message = "Example Message"))))
    val uiState: StateFlow<BocusUiState> = _uiState.asStateFlow()

    fun addAlarm(name:String, context: Context) {
        if (uiState.value.alarms.size < MAX_ALARMS) {
            val alarm = Alarm(id=getNextAlarmId(), name=name)
            _uiState.update { it.copy(alarms = it.alarms + alarm) }
        } else {
            setErrorMessage(context.getString(R.string.alarm_limit))
        }
    }
    fun goToScreen(screen:AppScreens) {
        _uiState.update { it.copy(currentScreen = screen, selectedAlarm = null) }
    }

    /// TODO: I should use some kinda guid and not walk the whole list each time
    fun getNextAlarmId(): Long {
        var highest:Long = 0
        for (alarm in _uiState.value.alarms) {
            highest = max(highest, alarm.id)
        }
        return highest+1
    }

    fun setErrorMessage(errorMessage: String) {
        _uiState.update { it.copy(errorMessage = errorMessage) }
    }

    fun dismissErrorDlg() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun openAlarmDetails(selectedAlarm: UByte) {
        _uiState.update { it.copy(selectedAlarm = selectedAlarm) }
    }

    fun closeAlarmDetails() {
        _uiState.update { it.copy(selectedAlarm = null) }
    }
}

data class BocusUiState (
    val alarms: List<Alarm> = listOf(),
    var currentScreen:AppScreens = AppScreens.WELCOME,
    /// Error Message
    var errorMessage: String? = null,
    /// index of the selected alarm
    var selectedAlarm: UByte? = null,
)