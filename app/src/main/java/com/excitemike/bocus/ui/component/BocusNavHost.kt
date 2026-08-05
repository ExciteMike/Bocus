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
    onError: (Int) -> Unit,
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
                        messageListScreenViewModel = messageListScreenViewModel,
                        onError = onError
                    )

                    AppScreens.ALARMS -> {
                        val defaultAlarmName = stringResource(R.string.default_alarm_name)
                        val defaultMessageListName =
                            stringResource(R.string.default_message_list_name)
                        val addAlarm =
                            { viewModel.addAlarm(defaultAlarmName, onError) }
                        val updateAlarm =
                            { alarm: Alarm -> viewModel.updateAlarmAndReschedule(alarm) }
                        val deleteAlarmById =
                            { alarmId: Long -> viewModel.deleteAlarmById(alarmId) }
                        val alarms = viewModel.alarmState.collectAsState().value
                        val messageLists =
                            messageListScreenViewModel.allMessageListsState.collectAsState().value
                        val addMessageList = {
                            messageListScreenViewModel.addMessageList(
                                defaultMessageListName,
                                onError = onError
                            )
                        }
                        AlarmScreen(
                            alarmScreenViewModel = alarmScreenViewModel,
                            alarms = alarms,
                            messageLists = messageLists,
                            addAlarm = addAlarm,
                            addMessageList = addMessageList,
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