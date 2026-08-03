package com.excitemike.bocus.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.AppScreens
import com.excitemike.bocus.data.INITIAL_APP_SCREEN
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.screen.AboutScreen
import com.excitemike.bocus.ui.screen.AlarmScreen
import com.excitemike.bocus.ui.screen.MessageListScreen
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.MessageListScreenViewModel

@Composable
fun BocusNavHost(
    navController: NavHostController,
    viewModel: BocusViewModel, // TODO: other viewmodels should take this one's responsibilities here
    alarmScreenViewModel: AlarmScreenViewModel,
    messageListScreenViewModel: MessageListScreenViewModel,
) {
    NavHost(
        navController,
        startDestination = INITIAL_APP_SCREEN.name
    ) {
        AppScreens.entries.forEach { screen ->
            composable(screen.name) {
                when (screen) {
                    AppScreens.ABOUT -> AboutScreen(
                        modifier = Modifier.fillMaxSize()
                    )

                    AppScreens.MESSAGE_LISTS -> MessageListScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = viewModel,
                        messageListScreenViewModel = messageListScreenViewModel,
                    )

                    AppScreens.ALARMS -> {
                        val defaultAlarmName = stringResource(R.string.default_alarm_name)
                        val addAlarm =
                            { viewModel.addAlarm(defaultAlarmName) }
                        val updateAlarm =
                            { alarm: Alarm -> viewModel.updateAlarmAndReschedule(alarm) }
                        val deleteAlarmById =
                            { alarmId: Long -> viewModel.deleteAlarmById(alarmId) }
                        val alarms = viewModel.alarmState.collectAsState().value
                        val messageLists = viewModel.messageListState.collectAsState().value
                        AlarmScreen(
                            alarmScreenViewModel = alarmScreenViewModel,
                            alarms = alarms,
                            messageLists = messageLists,
                            addAlarm = addAlarm,
                            updateAlarm = updateAlarm,
                            deleteAlarmById = deleteAlarmById,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}