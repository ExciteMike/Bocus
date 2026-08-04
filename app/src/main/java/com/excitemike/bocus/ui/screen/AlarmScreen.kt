package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.data.Alarm
import com.excitemike.bocus.data.MessageList
import com.excitemike.bocus.ui.dialog.AlarmDetailDialog
import com.excitemike.bocus.ui.component.AlarmGridItem
import com.excitemike.bocus.ui.component.GridWithAddButton
import com.excitemike.bocus.ui.viewmodel.AlarmScreenViewModel

@Composable
fun AlarmScreen(
    alarms: List<Alarm>,
    messageLists: List<MessageList>,
    addAlarm: () -> Unit,
    updateAlarm: (Alarm) -> Unit,
    deleteAlarmById: (Long) -> Unit,
    alarmScreenViewModel: AlarmScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val selectedAlarm = alarmScreenViewModel.selectedAlarmState.collectAsState().value

    if (selectedAlarm != null) {
        AlarmDetailDialog(
            selectedAlarm = selectedAlarm,
            messageLists = messageLists,
            updateAlarm = updateAlarm,
            close = {
                alarmScreenViewModel.clearSelectedAlarm()
            }
        )
    }

    GridWithAddButton(
        data = alarms,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_alarm),
        onAdd = addAlarm,
        modifier = modifier.fillMaxSize(),
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
