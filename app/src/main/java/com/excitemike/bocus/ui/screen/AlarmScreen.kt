package com.excitemike.bocus.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.excitemike.bocus.R
import com.excitemike.bocus.ui.BocusViewModel
import com.excitemike.bocus.ui.component.AlarmGridItem
import com.excitemike.bocus.ui.component.GridWithAddButton

@Composable
fun AlarmScreen(
    viewModel: BocusViewModel,
    modifier: Modifier = Modifier
) {
    val alarms = viewModel.alarmState.collectAsState().value
    val defaultAlarmName = stringResource(R.string.default_alarm_name)
    GridWithAddButton(
        data = alarms,
        dataKey = { it.id!! },
        addButtonLabel = stringResource(R.string.add_alarm),
        onAdd = { viewModel.addAlarm(defaultAlarmName) },
        modifier = modifier.fillMaxSize(),
        messageIfEmpty = stringResource(R.string.no_alarms)
    ) {
        AlarmGridItem(
            modifier = Modifier.height(104.dp),
            alarm = it,
            allAlarms = alarms,
            openAlarmDetails = { selectedAlarmIndex -> viewModel.openAlarmDetails(selectedAlarmIndex) },
            deleteAlarmById = { alarmId -> viewModel.deleteAlarmById(alarmId) }
        )
    }
}

