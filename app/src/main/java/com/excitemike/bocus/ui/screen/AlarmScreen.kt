package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.Message
import com.excitemike.bocus.ui.component.AlarmGridItem
import com.excitemike.bocus.ui.component.GridWithAddButton
import com.excitemike.bocus.ui.dialog.AlarmDetailDialog
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel
import com.excitemike.bocus.ui.viewmodel.MessagesViewModel

@Composable
fun AlarmScreen(
    alarmScreenViewModel: AlarmScreenViewModel,
    messagesViewModel: MessagesViewModel,
    onError: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val alarms = alarmScreenViewModel.allAlarmsState.collectAsState().value
    val selectedAlarm = alarmScreenViewModel.selectedAlarmState.collectAsState().value
    val defaultAlarmName = stringResource(R.string.default_alarm_name)
    val addAlarm =
        { alarmScreenViewModel.addAlarm(context, defaultAlarmName, onError) }
    val updateAlarm = { alarm: Alarm ->
        alarmScreenViewModel.updateAlarmAndReschedule(context, alarm)
    }
    val deleteAlarmById = { alarmId: Long ->
        alarmScreenViewModel.deleteAlarmById(context, alarmId)
    }

    if (selectedAlarm != null) {
        val alarmId = selectedAlarm.id!!
        LaunchedEffect(alarmId) {
            messagesViewModel.observeMessages(alarmId)
        }
        AlarmDetailDialog(
            selectedAlarm = selectedAlarm,
            messages =
                messagesViewModel.messagesByAlarmId.collectAsState().value[alarmId] ?: emptyList(),
            updateAlarm = updateAlarm,
            close = {
                alarmScreenViewModel.clearSelectedAlarm()
            },
            addMessage = {
                messagesViewModel.addMessage(alarmId, onError = onError)
            },
            deleteMessageById =
                { messageId: Long -> messagesViewModel.deleteMessageById(messageId) },
            updateMessage =
                { message: Message -> messagesViewModel.updateMessage(message) },
            observeMessages = { alarmId: Long ->
                messagesViewModel.observeMessages(alarmId)
            }
        )
    }

    GridWithAddButton(
        data = alarms,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_alarm),
        onAdd = addAlarm,
        modifier = modifier,
        messageIfEmpty = stringResource(R.string.no_alarms)
    ) { alarm ->
        AlarmGridItem(
            modifier = Modifier.height(104.dp),
            alarm = alarm,
            openAlarmDetails = {
                if (alarm.id != null) {
                    alarmScreenViewModel.loadSelectedAlarm(alarm.id)
                }
            },
            deleteAlarmById = deleteAlarmById
        )
    }
}
