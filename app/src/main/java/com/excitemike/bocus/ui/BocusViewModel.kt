package com.excitemike.bocus.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BocusViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(BocusUiState(alarms = listOf(Alarm("Test Alarm"))))
    val uiState: StateFlow<BocusUiState> = _uiState.asStateFlow()

    fun addAlarm(alarm:Alarm) {
        _uiState.update { it.copy(alarms = it.alarms + alarm) }
    }
    fun goToScreen(screen:AppScreens) {
        _uiState.update { it.copy(currentScreen = screen) }
    }
}

data class BocusUiState (
    val alarms: List<Alarm> = listOf(),
    var currentScreen:AppScreens = AppScreens.WELCOME,
)